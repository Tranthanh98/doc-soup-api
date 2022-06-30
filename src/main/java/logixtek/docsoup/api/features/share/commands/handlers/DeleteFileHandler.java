package logixtek.docsoup.api.features.share.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.commands.DeleteFile;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component("DeleteFileHandler")
@RequiredArgsConstructor()
public class DeleteFileHandler implements Command.Handler<DeleteFile, ResponseEntity<String>>{

    private final FileRepository fileRepository;
    private final DocumentRepository documentRepository;
    private final PermissionService permissionService;
    private  final DirectoryRepository directoryRepository;
    private final JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(DeleteFileHandler.class);


    @Override
    @Transactional
    public ResponseEntity<String> handle(DeleteFile command) {

        try {
            var fileOption = fileRepository.findById(command.getId());

            if (!fileOption.isPresent()) {
                return ResponseEntity.accepted().build();
            }

            var file = fileOption.get();

            var directoryOption =directoryRepository.findById(file.getDirectoryId());

            if(!directoryOption.isPresent())
            {
                return ResponseEntity.badRequest().build();
            }

            if (!permissionService.get(directoryOption.get(),command).canWrite()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if(!Objects.equals(file.getNda(), command.getNda()))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            var secureIds = new ArrayList<String>();
            var fileDocumentOption =documentRepository.findByFileIdWithVersion(file.getId());
            if(fileDocumentOption.isPresent())
            {
                secureIds.add(fileDocumentOption.get().getSecureId());
            }

            var linkDocumentOption = documentRepository.findAllByFileId(file.getId());

            if(linkDocumentOption.isPresent())
            {
                List<String> linkSecureIds = linkDocumentOption.get().stream().map(DocumentEntity::getSecureId).collect(Collectors.toList());
                secureIds.addAll(linkSecureIds);
            }

            var queueMessages = new ArrayList<JobMessage>();
            secureIds.forEach(item -> {
                var jobMessage = new JobMessage<String>();
                jobMessage.setAction(JobActionConstant.DELETE_DOCUMENT);
                jobMessage.setDataBody(item);
                jobMessage.setObjectName(String.class.getName());
                queueMessages.add(jobMessage);
            });

            publisher.sendMessageBatch(queueMessages);

           fileRepository.deleteAllById(file.getId());

            return ResponseEntity.accepted().build();
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);

            return  ResponseEntity.unprocessableEntity().build();
        }

    }


}
