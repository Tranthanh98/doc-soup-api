package logixtek.docsoup.api.features.dataroom.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.DuplicateDataRoom;
import logixtek.docsoup.api.features.dataroom.mappers.DataRoomMapper;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("DuplicateDataRoomHandler")
@AllArgsConstructor
public class DuplicateDataRoomHandler implements Command.Handler<DuplicateDataRoom, ResponseMessageOf<Long>> {

    private final DataRoomRepository dataRoomRepository;
    private final DataRoomContentRepository dataRoomContentRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseMessageOf<Long> handle(DuplicateDataRoom command) {

        if (!dataRoomLimitationService.isDuplicate(command.getCompanyId(), command.getId())) {
            return new ResponseMessageOf<>(HttpStatus.UNPROCESSABLE_ENTITY, ResponseResource.LimitedPlanExceeded,
                    Map.of());
        }

        var dataRoomOption = dataRoomRepository.findById(command.getId());

        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("data room doesn't exist",
                    Map.of("id", "data room doesn't exist"));
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var dataRoom = dataRoomOption.get();

        var dataRoomEntity = DataRoomMapper.INSTANCE.toEntity(dataRoom);
        dataRoomEntity.setName(command.getName());

        var dataRoomDuplicated = dataRoomRepository.saveAndFlush(dataRoomEntity);

        dataRoomContentRepository.duplicateDataRoomContent(command.getId(), dataRoomDuplicated.getId());

        return ResponseMessageOf.of(HttpStatus.CREATED, dataRoomDuplicated.getId());
    }
}
