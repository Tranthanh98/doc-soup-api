package logixtek.docsoup.api.features.link;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.commands.*;
import logixtek.docsoup.api.features.link.queries.*;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("link")
public class LinkController extends BaseController {

    public LinkController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService ) {
        super(pipeline, authenticationManager,accountService);
    }

    @PostMapping
    public ResponseEntity<?> createLink(@Valid  @RequestBody CreateLink command, BindingResult bindingResult){
        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("{id}/setting")
    public ResponseEntity<?> updateSetting(@PathVariable UUID id, @Valid  @RequestBody UpdateLinkSetting command,
                                           BindingResult bindingResult){
        command.setLinkId(id);
        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/{id}/add-allow-viewer")
    public ResponseEntity<?> AddAllowViewer(@PathVariable UUID id, @Valid  @RequestBody AddAllowViewer command,
                                           BindingResult bindingResult){
        command.setLinkId(id);
        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateLinkStatus(@PathVariable UUID id, @Valid @RequestBody UpdateLinkStatus command, BindingResult bindingResult){
        command.setLinkId(id);
        return handleWithResponse(command, bindingResult);
    }

    @GetMapping("/{id}/setting")
    public ResponseEntity<?> getSetting(@PathVariable UUID id){
        var query =  GetLinkSetting.of(id);
        return handleWithResponseMessage(query);
    }

    @GetMapping("/{id}/viewer")
    public ResponseEntity<Collection<Viewer>> getViewer(@PathVariable UUID id){
        var query = new ListViewerOfLink(id);
        return handle(query);
    }

    @GetMapping("/{id}/viewer/{viewerId}/statistic")
    public ResponseEntity<Collection<PageStatistic>> getViewerStatistic(@PathVariable UUID id, @PathVariable Long viewerId){
        var query = ListPageStatisticOfViewerOnLink.of(viewerId,id);
        return handle(query);
    }

    @GetMapping("/{id}/viewer/{viewerId}/statistic/{pageNumber}/thumb")
    public ResponseEntity<?> getPageThumbnail(@PathVariable UUID id, @PathVariable Long viewerId,@PathVariable Integer pageNumber){
        var query = GetLinkThumbnailPage.of(id,pageNumber);
        return handleWithResponse(query);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        var command = DeleteLink.of(id);
        return handleWithResponseMessage(command);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLink(@Valid SearchLink query, BindingResult bindingResult){
        return handleWithResponse(query, bindingResult);
    }

    @GetMapping("/count-all-link-by-company")
    public ResponseEntity<Integer> countAllLinkByCompany() {
        var query = new GetTotalLinks();
        return handleWithResponse(query);
    }
}
