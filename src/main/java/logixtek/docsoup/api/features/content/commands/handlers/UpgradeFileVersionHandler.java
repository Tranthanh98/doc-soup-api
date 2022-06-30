package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import logixtek.docsoup.api.features.content.commands.UpgradeFileVersion;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.dtos.ReregisterDocumentRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.ContentLimitationService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.helper.ContentHelper;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("UpgradeVersionFileHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpgradeFileVersionHandler implements Command.Handler<UpgradeFileVersion, ResponseMessageOf<Long>> {

    private final FileRepository fileRepository;
    private final FileContentRepository fileContentRepository;
    private final DocumentRepository documentRepository;
    private final JobMessageQueuePublisher publisher;
    private final ContentLimitationService contentLimitationService;

    @SneakyThrows
    @Override
    public ResponseMessageOf<Long> handle(UpgradeFileVersion command) {

        if(!contentLimitationService.canUpdate(command.getCompanyId(), command.getAccountId(), command.getMultipartFile())){
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, "Plan Limitations Exceeded", Map.of());
        }

        if (command.getMultipartFile().isEmpty()) {
            return new ResponseMessageOf<>(HttpStatus.BAD_REQUEST, "NO data",
                    Map.of(UploadFileCommand.Fields.multipartFile, "NO DATA"));
        }

        var contentType = command.getMultipartFile().getContentType();

        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            return new ResponseMessageOf<>(HttpStatus.BAD_REQUEST, "Invalid file type",
                    Map.of(UploadFileCommand.Fields.multipartFile, "Invalid file type"));
        }

        var fileOption = fileRepository.findById(command.getFileId());

        if (!fileOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var file = fileOption.get();

        var fileContentOption = fileContentRepository.findById(file.getId());

        if (!fileContentOption.isPresent()) {
            return new ResponseMessageOf<>(HttpStatus.NOT_FOUND,
                    "File content not found",
                    Map.of());
        }

        var fileContent = fileContentOption.get();

        var content = ContentHelper.convertFileToBlob(command.getMultipartFile());
        fileContent.setContent(content);

        fileContentRepository.saveAndFlush(fileContent);

        var newVersion = file.getVersion() + 1;

        file.setSize(command.getMultipartFile().getSize());
        file.setVersion(newVersion);

        fileRepository.saveAndFlush(file);
        updateDocumentEntity(file);

        return ResponseMessageOf.of(HttpStatus.OK);

    }

    private void updateDocumentEntity(FileEntity file) throws JsonProcessingException {

        var queueMessages = new ArrayList<ReregisterDocumentRequestMessage>();

        var allDocumentsOfFileOption = documentRepository
                .findAllDocumentWithSettingValueByFileId(file.getId());

        if(allDocumentsOfFileOption.isPresent()) {
            allDocumentsOfFileOption.get().forEach(item -> {
                var lifeSpan = Utils.calculateLifeSpan(Instant.now(), item.getLifeSpan().toInstant());
                var registerDocumentQueueModel =  ReregisterDocumentRequestMessage.builder()
                        .documentId(item.getId())
                        .fileId(file.getId())
                        .isFile(false)
                        .download(item.getDownload())
                        .expiredAt(item.getExpiredAt())
                        .lifeSpan(lifeSpan)
                        .build();
                queueMessages.add(registerDocumentQueueModel);
            });
        }

        var documentOption = documentRepository.findFirstByFileIdAndFileVersionAndRefIdIsNull(file.getId(), file.getVersion());

        if (documentOption.isPresent()) {
            var lifeSpan = Utils.calculateLifeSpan(Instant.now(), documentOption.get().getExpiredAt().toInstant());
            queueMessages.add(ReregisterDocumentRequestMessage.builder()
                    .fileId(file.getId())
                    .documentId(documentOption.get().getId())
                    .isFile(true)
                    .expiredAt(documentOption.get().getExpiredAt())
                    .lifeSpan(lifeSpan)
                    .build()
            );
        }

        List<JobMessage> queueData = new ArrayList<>();

        for(var data : queueMessages){
            var jobMessage = new JobMessage<ReregisterDocumentRequestMessage>();
            jobMessage.setObjectName(ReregisterDocumentRequestMessage.class.getName());
            jobMessage.setDataBody(data);
            jobMessage.setAction(JobActionConstant.NEW_DOCUMENT_VERSION);
            queueData.add(jobMessage);
        }

        publisher.sendMessageBatch(queueData);
    }
}

