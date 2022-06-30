package logixtek.docsoup.api.features.administrator.publicAccount;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.publicAccount.commands.EnableOrDisableAccount;
import logixtek.docsoup.api.features.administrator.publicAccount.queries.GetPublicAccount;
import logixtek.docsoup.api.features.administrator.publicAccount.queries.ListPublicAccount;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("internal/public-account")
public class PublicAccountController extends BaseAdminController {
    public PublicAccountController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping
    public ResponseEntity<?> getAccountsDocsoup(@Valid ListPublicAccount query, BindingResult bindingResult){

        return handleWithResponse(query,bindingResult);

    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable String accountId){

        return handleWithResponse(new GetPublicAccount(accountId));

    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> enableOrDisableAccount(@Valid @RequestBody EnableOrDisableAccount command, @PathVariable String accountId){

        command.setAccountId(accountId);

        return handleWithResponseMessage(command);
    }
}
