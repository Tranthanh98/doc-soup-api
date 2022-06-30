package logixtek.docsoup.api.features.link.linkAccount;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.linkAccount.commands.CreateLinkAccount;
import logixtek.docsoup.api.features.link.linkAccount.commands.MergeLinkAccount;
import logixtek.docsoup.api.features.link.linkAccount.commands.UpdateLinkAccountStatus;
import logixtek.docsoup.api.features.link.linkAccount.queries.*;
import logixtek.docsoup.api.features.link.linkAccount.queries.SearchLinkAccount;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.models.LinkAccountVisitor;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("link/link-account")
public class LinkAccountController extends BaseController {
    public LinkAccountController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService ) {
        super(pipeline, authenticationManager,accountService);
    }

    @PostMapping
    public ResponseEntity<?> createLinkAccount(@Valid @RequestBody CreateLinkAccount command, BindingResult bindingResult){
        return handleWithResponse(command, bindingResult);
    }

    @GetMapping("suggestion-search")
    public ResponseEntity<?> getLinkAccountSuggestion(@RequestParam String keyword){
        var query = GetLinkAccountNameSuggestion.of(keyword);
        return handleWithResponse(query);
    }

    @GetMapping
    public ResponseEntity<?> getListLinkAccount(@RequestParam(required = false) String status, @RequestParam String mode, @RequestParam(required = false) Boolean archived) {
        var query = new GetListLinkAccount();
        query.setStatus(status);
        query.setArchived(archived);
        query.setMode(mode);

        return handleWithResponse(query);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateLinkAccountStatus(@Valid @RequestBody UpdateLinkAccountStatus command, BindingResult bindingResult, @PathVariable Long id) {
        command.setId(id);
        return handleWithResponse(command, bindingResult);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<?> getListLinkOfLinkAccount(@PathVariable Long id,
                                                      @RequestParam String filter,
                                                      @RequestParam Integer page,
                                                      @RequestParam Integer pageSize){
        var query = GetListLinkOfLinkAccount.of(id, filter);
        query.setPage(page);
        query.setPageSize(pageSize);

        return handleWithResponse(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLinkAccountById(@PathVariable Long id){
        var query = GetLinkAccount.of(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/visitor")
    public ResponseEntity<PageResultOf<LinkAccountVisitor>> getVisitorsOfLinkAccount(@PathVariable Long id,
                                                                                     @RequestParam Integer page,
                                                                                     @RequestParam Integer pageSize){
        var query = GetVisitorOfLinkAccount.of(id);
        query.setPage(page);
        query.setPageSize(pageSize);

        return handleWithResponse(query);
    }

    @PostMapping("/{sourceLinkAccountId}/merge")
    public ResponseEntity<?> mergeLinkAccount(@Valid @RequestBody MergeLinkAccount command, BindingResult bindingResult, @PathVariable Long sourceLinkAccountId) {
        command.setSourceLinkAccountId(sourceLinkAccountId);
        return handleWithResponseMessage(command, bindingResult);
    }

    @GetMapping("/{linkAccountId}/data-room")
    public ResponseEntity<?> getListDataRoomOfLinkAccount(@PathVariable Long linkAccountId) {
        var query = GetListDataRoomOfLinkAccount.of(linkAccountId);
        return handleWithResponseMessage(query);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLinkAccount(@Valid SearchLinkAccount query, BindingResult bindingResult){

        return handleWithResponse(query, bindingResult);
    }
}
