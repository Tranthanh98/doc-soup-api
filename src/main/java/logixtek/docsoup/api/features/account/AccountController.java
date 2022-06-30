package logixtek.docsoup.api.features.account;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.account.commands.*;
import logixtek.docsoup.api.features.account.queries.GetAccount;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("account")
public class AccountController extends BaseController {

    public AccountController(Pipeline pipeline, AuthenticationManager authenticationManager,
                             AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @GetMapping
    public ResponseEntity<?> getAccount() {
        var query = new GetAccount();
        return handleWithResponse(query);
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(@Valid @RequestBody UpdateAccount command) {
        return handleWithResponseMessage(command);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword command) {
        return handleWithResponseMessage(command);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword() {
        var command = new ForgotPassword();

        return handleWithResponseMessage(command);
    }

    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPassword command, BindingResult bindingResult) {
        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/switch-company")
    public ResponseEntity<?> switchCompany(@Valid @RequestBody SwitchCompany command, BindingResult bindingResult) {
        return handleWithResponse(command, bindingResult);
    }

    @PutMapping("/update-notification-setting")
    public ResponseEntity<?> switchCompany(@Valid @RequestBody UpdateNotificationSetting command, BindingResult bindingResult) {
        return handleWithResponse(command, bindingResult);
    }

}
