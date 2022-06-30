package logixtek.docsoup.api.features.dataroom;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.dataroom.commands.*;
import logixtek.docsoup.api.features.dataroom.queries.*;
import logixtek.docsoup.api.features.dataroom.responses.DataRoomDetail;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.models.DataRoomInfo;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("data-room")
public class DataRoomController extends BaseController {

    public DataRoomController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @GetMapping
    public ResponseEntity<List<DataRoomInfo>> listAllDataRoom(@RequestParam String filter) {
        var query = new ListAll();
        query.setFilter(filter);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataRoomDetail> getDataRoom(@PathVariable Long id) {
        var query = Get.of(id);

        return handleWithResponse(query);
    }

    @GetMapping("/{id}/link")
    public ResponseEntity<?> listLink(@PathVariable Long id) {
        var query = ListDataRoomLink.of(id);
        return handleWithResponse(query);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Create command, BindingResult bindingResult) {

        return handleWithResponseMessage(command, bindingResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        var command = Delete.of(id);
        return handleWithResponseMessage(command);
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<?> duplicate(@Valid @RequestBody DuplicateDataRoom command, BindingResult bindingResult, @PathVariable Long id) {
        command.setId(id);

        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDataRoom(@PathVariable Long id,
                                            @Valid @RequestBody UpdateDataRoom command,
                                            BindingResult bindingResult) {
        command.setId(id);

        return handle(command, bindingResult);
    }


    @PutMapping("/{id}/link-status")
    public ResponseEntity<?> updateAllLinkStatus(@PathVariable Long id, @Valid @RequestBody UpdateAllLinkStatus command) {
        command.setId(id);

        return handleWithResponseMessage(command);
    }

    @GetMapping("/{dataRoomId}/export-viewer")
    public ResponseEntity<?> exportViewer(@PathVariable Long dataRoomId, @RequestParam Boolean email) {
        return handleWithResponse(ExportViewerOfDataRoom.of(dataRoomId, email));
    }

    @PutMapping("/{dataRoomId}/content/{contentId}/order-no")
    public ResponseEntity<?> changeDataRoomContentOrderNo(@PathVariable Long dataRoomId,
                                                          @PathVariable Long contentId,
                                                          @Valid @RequestBody ChangeOrderNoOfContent command,
                                                          BindingResult bindingResult) {
        command.setId(dataRoomId);
        command.setContentId(contentId);

        return handleWithResponseMessage(command, bindingResult);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDataRoom(@Valid SearchDataRoom query, BindingResult bindingResult) {
        return handleWithResponse(query, bindingResult);
    }
}
