package logixtek.docsoup.api.features.dataroom.content;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.dataroom.content.commands.AddContent;
import logixtek.docsoup.api.features.dataroom.content.commands.DeleteDataRoomContent;
import logixtek.docsoup.api.features.dataroom.content.commands.UpdateDataRoomContentStatus;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("data-room/{dataRoomId}")
public class DataRoomContentController extends BaseController {
    public DataRoomContentController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @PostMapping
    public ResponseEntity<?> addContent(@PathVariable Long dataRoomId, @RequestBody AddContent command) {
        command.setId(dataRoomId);
        return handleWithResponseMessage(command);
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<?> deleteDataRoomContent(@PathVariable Long dataRoomId, @PathVariable Long contentId) {
        var command = DeleteDataRoomContent.of(dataRoomId, contentId);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/content/{contentId}/status")
    public ResponseEntity<?> updateDataRoomContentStatus(@PathVariable Long dataRoomId,
                                                         @PathVariable Long contentId,
                                                         @RequestBody UpdateDataRoomContentStatus command) {
        command.setContentId(contentId);
        command.setDataRoomId(dataRoomId);
        return handleWithResponseMessage(command);
    }
}
