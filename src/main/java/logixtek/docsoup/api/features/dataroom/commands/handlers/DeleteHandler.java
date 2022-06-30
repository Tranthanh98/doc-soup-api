package logixtek.docsoup.api.features.dataroom.commands.handlers;


import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.Delete;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component("DeleteDataRoomHandler")
@AllArgsConstructor
public class DeleteHandler implements Command.Handler<Delete, ResponseMessageOf<Long>> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteHandler.class);
    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Override
    @Transactional
    public ResponseMessageOf<Long> handle(Delete command) {

        var isExistDataRoom = dataRoomRepository.findById(command.getId());

        if (!isExistDataRoom.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        if (!permissionService.getOfDataRoom(isExistDataRoom.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var documentOption = documentRepository.findAllByDataRoomId(command.getId());

        if (documentOption.isPresent()) {

            try {
                for (DocumentEntity doc : documentOption.get()) {
                    if (doc.getSecureId() != null) {
                        documentService.Delete(doc.getSecureId());
                    }
                }
            } catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
            }

        }

        dataRoomRepository.deleteAllById(command.getId());

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
