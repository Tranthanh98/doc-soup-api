package logixtek.docsoup.api.features.contact;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.contact.commands.UpdateContactStatus;
import logixtek.docsoup.api.features.contact.queries.*;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.models.PageStatisticOnContact;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("contact")
public class ContactController extends BaseController {

    public ContactController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping
    public ResponseEntity<?> listAll(@RequestParam Boolean includeArchived, @RequestParam String mode){
        var query = new ListAllContact();
        query.setMode(mode);
        query.setIncludeArchived(includeArchived);

        return handleWithResponse(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){

        var query = GetContact.of(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> listFile(@PathVariable Long id){

        var query = ListFileOfContact.of(id);
        return handleWithResponse(query);
    }


    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archive(@PathVariable Long id, @Valid @RequestBody UpdateContactStatus command)
    {
        command.setContactId(id);
        return  handleWithResponse(command);
    }

    @GetMapping("/{id}/statistic/{fileId}")
    public ResponseEntity<Collection<PageStatisticOnContact>> getViewerStatistic(@PathVariable Long id, @PathVariable Long fileId){
        var query = new ListLinkWithStatisticOfFile(id, fileId);
        return handle(query);
    }

    @GetMapping("search")
    public ResponseEntity<?> searchContact(@Valid SearchContact query, BindingResult bindingResult){
        return handleWithResponse(query, bindingResult);
    }
}
