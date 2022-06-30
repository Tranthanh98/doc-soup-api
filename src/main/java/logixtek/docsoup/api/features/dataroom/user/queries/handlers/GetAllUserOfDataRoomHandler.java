package logixtek.docsoup.api.features.dataroom.user.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.user.queries.GetAllUserOfDataRoom;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.DataRoomUser;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("GetAllUserOfDataRoomHandler")
@AllArgsConstructor
public class GetAllUserOfDataRoomHandler implements Command.Handler<GetAllUserOfDataRoom, ResponseMessageOf<List<DataRoomUser>>> {
    private final DataRoomRepository dataRoomRepository;
    private final DataRoomUserRepository dataRoomUserRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseMessageOf<List<DataRoomUser>> handle(GetAllUserOfDataRoom query) {
        var dataRoomOption = dataRoomRepository.findById(query.getDataRoomId());
        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest("data room doesn't exist",
                    Map.of("id", "data room doesn't exist"));
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), query).canRead()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var dataRoomUser = dataRoomUserRepository.findAllUserOfDataRoom(query.getDataRoomId());

        if (dataRoomUser.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.OK, dataRoomUser.get());
        }

        return ResponseMessageOf.of(HttpStatus.OK, Collections.emptyList());
    }
}
