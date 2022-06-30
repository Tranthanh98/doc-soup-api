package logixtek.docsoup.api.features.dataroom.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.dataroom.user.commands.DeleteDataRoomUser;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("DeleteDataRoomUserHandler")
@AllArgsConstructor
public class DeleteDataRoomUserHandler implements Command.Handler<DeleteDataRoomUser, ResponseMessageOf<Long>> {
    private final DataRoomRepository dataRoomRepository;
    private final DataRoomUserRepository dataRoomUserRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseMessageOf<Long> handle(DeleteDataRoomUser command) {
        var dataRoomOption = dataRoomRepository.findById(command.getDataRoomId());
        if (!dataRoomOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        if (!permissionService.getOfDataRoom(dataRoomOption.get(), command).canWrite() || dataRoomOption.get().getAccountId().equals(command.getUserId())) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var dataRoomUserOption = dataRoomUserRepository.findByUserIdAndDataRoomId(command.getUserId(), command.getDataRoomId());

        if (!dataRoomUserOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        dataRoomUserRepository.deleteById(dataRoomUserOption.get().getId());

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
