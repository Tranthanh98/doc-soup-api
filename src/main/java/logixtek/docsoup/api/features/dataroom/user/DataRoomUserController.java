package logixtek.docsoup.api.features.dataroom.user;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.dataroom.user.commands.CreateDataRoomUser;
import logixtek.docsoup.api.features.dataroom.user.commands.DeleteDataRoomUser;
import logixtek.docsoup.api.features.dataroom.user.queries.GetAllUserOfDataRoom;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("data-room/{dataRoomId}/user")
public class DataRoomUserController extends BaseController {
    public DataRoomUserController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @GetMapping()
    public ResponseEntity<?> getAllUserOfDataRoom(@PathVariable Long dataRoomId) {
        var query = GetAllUserOfDataRoom.of(dataRoomId);
        return handleWithResponseMessage(query);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> createUser(@PathVariable Long dataRoomId, @PathVariable String userId) {
        var command = CreateDataRoomUser.of(dataRoomId, userId);
        command.setDataRoomId(dataRoomId);
        return handleWithResponseMessage(command);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long dataRoomId, @PathVariable String userId) {
        var command = DeleteDataRoomUser.of(dataRoomId, userId);
        return handleWithResponseMessage(command);
    }
}
