package logixtek.docsoup.api.features.dataroom.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import logixtek.docsoup.api.features.dataroom.queries.ExportViewerOfDataRoom;
import logixtek.docsoup.api.features.share.domainevents.ExportDataRoomViewerDomainEvent;
import logixtek.docsoup.api.features.share.dtos.SendEmailDataRoomVisitLinkRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.DownloadConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;

@Component("ExportDataRoomViewerDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExportDataRoomViewerDomainEventHandler implements Notification.Handler<ExportDataRoomViewerDomainEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ExportDataRoomViewerDomainEventHandler.class);
    public final JobMessageQueuePublisher publisher;
    private final Pipeline pipeline;
    private final AccountService accountService;
    private final DataRoomRepository dataRoomRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @SneakyThrows
    @Override
    public void handle(ExportDataRoomViewerDomainEvent notification) {
        var account = accountService.get(notification.getAccountId());
        if (account != null) {
            var dataRoomOption = dataRoomRepository.findById(notification.getDataRoomId());
            if (dataRoomOption.isPresent()) {
                var dataRoom = dataRoomOption.get();
                var exportViewerOfDataRoom = ExportViewerOfDataRoom.of(dataRoom.getId(), false);
                exportViewerOfDataRoom.setAccountId(account.getId());
                var exportViewerOfDataRoomResult = pipeline.send(exportViewerOfDataRoom);
                if (exportViewerOfDataRoomResult.getBody() != null && exportViewerOfDataRoomResult.getStatusCode() == HttpStatus.OK) {
                    var fileContent = exportViewerOfDataRoomResult.getBody();
                    if (fileContent != null) {
                        var bucketKey = DownloadConstant.GuestFolder + "/" + dataRoom.getName() + "_" + Instant.now().toEpochMilli();
                        try (InputStream fileStream = new ByteArrayInputStream(fileContent)) {
                            var metaData = new ObjectMetadata();
                            metaData.setContentLength(fileContent.length);
                            metaData.setContentType("text/csv");
                            var putObjectRequest = new PutObjectRequest(bucketName, bucketKey, fileStream, metaData);
                            amazonS3.putObject(putObjectRequest);

                            var userFullName = account.getFirstName() + " " + account.getLastName();

                            var jobMessage = new JobMessage<SendEmailDataRoomVisitLinkRequestMessage>();
                            jobMessage.setAction(JobActionConstant.SEND_EMAIL_DATA_ROOM_VISIT_LINK);
                            jobMessage.setObjectName(SendEmailDataRoomVisitLinkRequestMessage.class.getName());
                            var body = SendEmailDataRoomVisitLinkRequestMessage.of(userFullName, bucketKey, account.getEmail(), dataRoom.getName());
                            jobMessage.setDataBody(body);

                            try {
                                publisher.sendMessage(jobMessage);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        } catch (Exception ex) {
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
    }
}
