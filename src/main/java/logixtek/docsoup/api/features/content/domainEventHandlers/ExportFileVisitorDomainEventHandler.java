package logixtek.docsoup.api.features.content.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import logixtek.docsoup.api.features.content.queries.ExportViewerOfFile;
import logixtek.docsoup.api.features.share.domainevents.ExportFileVisitorDomainEvent;
import logixtek.docsoup.api.features.share.dtos.SendEmailExportFileVisitorRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.DownloadConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;

@Component("ExportFileVisitorDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExportFileVisitorDomainEventHandler implements Notification.Handler<ExportFileVisitorDomainEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ExportFileVisitorDomainEventHandler.class);
    public final JobMessageQueuePublisher publisher;
    private final Pipeline pipeline;
    private final AccountService accountService;
    private final FileRepository fileRepository;
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;
    @Override
    public void handle(ExportFileVisitorDomainEvent notification) {
        var account = accountService.get(notification.getAccountId());
        if (account != null) {
            var fileOption = fileRepository.findById(notification.getFileId());
            if(fileOption.isPresent()) {
                var file = fileOption.get();
                var exportViewerOfFile = ExportViewerOfFile.of(file.getId(), false);
                exportViewerOfFile.setAccountId(file.getAccountId());
                exportViewerOfFile.setCompanyId(file.getCompanyId());

                var exportFileVisitorResult = exportViewerOfFile.execute(pipeline);

                if (exportFileVisitorResult.getBody() != null && exportFileVisitorResult.getStatusCode() == HttpStatus.OK) {
                    var fileContent = exportFileVisitorResult.getBody();
                    var bucketKey = DownloadConstant.GuestFolder + "/" + file.getId() + "_" + Instant.now().toEpochMilli();
                    try (InputStream fileStream = new ByteArrayInputStream(fileContent)) {
                        var metaData = new ObjectMetadata();
                        metaData.setContentLength(fileContent.length);
                        metaData.setContentType("text/csv");
                        var putObjectRequest = new PutObjectRequest(bucketName, bucketKey, fileStream, metaData);
                        amazonS3.putObject(putObjectRequest);

                        var userFullName = account.getFirstName() + " " + account.getLastName();

                        var jobMessage = new JobMessage<SendEmailExportFileVisitorRequestMessage>();
                        jobMessage.setAction(JobActionConstant.SEND_EMAIL_FILE_VISITOR);
                        jobMessage.setObjectName(SendEmailExportFileVisitorRequestMessage.class.getName());
                        var body = SendEmailExportFileVisitorRequestMessage.of(userFullName, bucketKey, account.getEmail(), file.getName());
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
