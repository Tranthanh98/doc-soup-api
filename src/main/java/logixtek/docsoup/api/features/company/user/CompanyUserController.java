package logixtek.docsoup.api.features.company.user;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.company.user.commands.*;
import logixtek.docsoup.api.features.company.user.queries.ExportUsers;
import logixtek.docsoup.api.features.company.user.queries.GetListCompanyOfUser;
import logixtek.docsoup.api.features.company.user.queries.GetUserTeam;
import logixtek.docsoup.api.features.company.user.queries.ListCompanyUser;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("company")
public class CompanyUserController extends BaseController {
    public CompanyUserController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/{companyId}/user")
    public ResponseEntity<?> getUsers(@PathVariable UUID companyId)
    {
        var query = ListCompanyUser.of(companyId);
        return  handleWithResponse(query);
    }

    @PostMapping("/{companyId}/user")
    public ResponseEntity<?> addUsers(@PathVariable UUID companyId, @Valid @RequestBody AddBatchCompanyUser command)
    {
        command.setCompanyId(companyId);
        return  handleWithResponseMessage(command);
    }

    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    @PostMapping("/user/accept-invitation")
    public ResponseEntity<?> acceptInvitation(@Valid @RequestBody AcceptInvitation command, @RequestParam String token)
    {
        command.setToken(token);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/{companyId}/user/{targetAccountId}/de-active")
    public ResponseEntity<?> deActiveUser(@PathVariable UUID companyId, @PathVariable String targetAccountId){
        var command = new DeactivateUser();
        command.setCompanyId(companyId);
        command.setTargetAccountId(targetAccountId);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/{companyId}/user/{targetAccountId}/re-active")
    public ResponseEntity<?> reActiveUser(@PathVariable UUID companyId, @PathVariable String targetAccountId){
        var command = new ReactiveUser();
        command.setCompanyId(companyId);
        command.setTargetAccountId(targetAccountId);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/{companyId}/user/{targetAccountId}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable UUID companyId, @PathVariable String targetAccountId){
        var command = new SuspendUser();
        command.setCompanyId(companyId);
        command.setTargetAccountId(targetAccountId);
        return handleWithResponseMessage(command);
    }

    @PostMapping("/{companyId}/user/{sourceAccountId}/transfer-data-to")
    public ResponseEntity<?> transferDataTo(@PathVariable UUID companyId, @PathVariable String sourceAccountId, @Valid @RequestBody TransferData command, BindingResult bindingResult){
        command.setCompanyId(companyId);
        command.setSourceAccountId(sourceAccountId);
        return handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/{companyId}/user/{targetAccountId}/make-owner")
    public ResponseEntity<?> makeUserOwner(@PathVariable UUID companyId,
                                           @PathVariable String targetAccountId){

        var command = new MakeUserOwner();
        command.setCompanyId(companyId);
        command.setTargetAccountId(targetAccountId);

        return handleWithResponse(command);
    }

    @GetMapping()
    public ResponseEntity<?> getCompaniesOfUser()
    {
        var query = new GetListCompanyOfUser();
        return handleWithResponse(query);
    }
    @PutMapping("/{companyId}/user/{userId}/make-member")
    public ResponseEntity<?> makeUserMember(@PathVariable UUID companyId,
                                           @PathVariable String userId){

        var command = new MakeUserMember();
        command.setCompanyId(companyId);
        command.setUserId(userId);

        return handleWithResponse(command);
    }

    @GetMapping("/{companyId}/export-user-list")
    public ResponseEntity<?> exportUsers(@PathVariable UUID companyId){
        var query = new ExportUsers();
        query.setCompanyId(companyId);
        return handleWithResponse(query);
    }

    @PutMapping("/{companyId}/user/{targetAccountId}/unsuspend")
    public ResponseEntity<?> unsuspendUser(@PathVariable UUID companyId, @PathVariable String targetAccountId){
        var command = new UnsuspendUser();
        command.setCompanyId(companyId);
        command.setTargetAccountId(targetAccountId);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/user/cancel-invitation/{companyUserId}")
    public ResponseEntity<?> cancelInvitation(@PathVariable Long companyUserId){
        var command = new CancelInvitation();
        command.setTargetCompanyUserId(companyUserId);
        return handleWithResponseMessage(command);
    }

    @PutMapping("/user/resend-invitation/{companyUserId}")
    public ResponseEntity<?> resendInvitation(@PathVariable Long companyUserId){
        var command = new ResendInvitation();
        command.setTargetCompanyUserId(companyUserId);
        return handleWithResponseMessage(command);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountInfoById(@PathVariable String userId){
        var query = GetUserTeam.of(userId);
        return handleWithResponse(query);
    }
}
