package logixtek.docsoup.api.features.dataroom.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.content.commands.DeleteDataRoomContent;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@Component("DeleteDataRoomContentHandler")
@AllArgsConstructor
public class DeleteDataRoomContentHandler implements Command.Handler<DeleteDataRoomContent, ResponseMessageOf<Long>> {
    private final DataRoomContentRepository dataRoomContentRepository;
    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    @Transactional
    public ResponseMessageOf<Long> handle(DeleteDataRoomContent command) {

        if (!dataRoomLimitationService.isAllow(command.getCompanyId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        var dataRoomOption = dataRoomRepository.findById(command.getDataRoomId());

        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        Optional<DataRoomContentEntity> dataRoomContentOption = dataRoomContentRepository.findById(command.getContentId());

        if (!dataRoomContentOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var dataRoomContent = dataRoomContentOption.get();

        dataRoomContentRepository.delete(dataRoomContent);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}