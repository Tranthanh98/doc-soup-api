package logixtek.docsoup.api.features.dataroom.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.commands.UpdateDataRoom;
import logixtek.docsoup.api.features.dataroom.mappers.DataRoomMapper;
import logixtek.docsoup.api.features.share.services.DataRoomLimitationService;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateDataRoomHandler")
@AllArgsConstructor
public class UpdateDataRoomHandler implements Command.Handler<UpdateDataRoom, ResponseEntity<String>> {

    private final DataRoomRepository dataRoomRepository;
    private final PermissionService permissionService;
    private final DataRoomLimitationService dataRoomLimitationService;

    @Override
    public ResponseEntity<String> handle(UpdateDataRoom command) {
        if (!dataRoomLimitationService.isAllow(command.getCompanyId())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ResponseResource.LimitedPlanExceeded);
        }

        var dataRoomOption = dataRoomRepository
                .findByIdAndAccountIdAndCompanyId(command.getId(), command.getAccountId(), command.getCompanyId());

        if (dataRoomOption.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var dataRoom = dataRoomOption.get();

        if (!permissionService.get(dataRoom, command).canWrite()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var updatedDataRoom = DataRoomMapper.INSTANCE.updateEntity(dataRoom, command);

        dataRoomRepository.saveAndFlush(updatedDataRoom);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
