package logixtek.docsoup.api.infrastructure.controllers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.commands.IRequiredUserValidationCommand;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import logixtek.docsoup.api.infrastructure.response.ResponseMessage;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuthorize("hasRole('public_site')")
public class BaseController {

    private final Pipeline pipeline;

    protected final AuthenticationManager authenticationManager;

    private final AccountService accountService;

    @Autowired
    CompanyUserCacheService companyUserCacheService;

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    protected String getAccountId() {
        var token = (Jwt) this.authenticationManager.getAuthentication().getPrincipal();

        return token.getClaim("sub");
    }

    protected AccountEntity getAccountFromToken() {
        var token = (Jwt) this.authenticationManager.getAuthentication().getPrincipal();

        var entity = new AccountEntity();

        entity.setId(token.getClaim("sub"));
        entity.setEmail(token.getClaim("email"));
        entity.setFirstName(token.getClaim("given_name"));
        entity.setLastName(token.getClaim("family_name"));

        return entity;
    }

    protected AccountEntity getAccountInfo() {
        var accountId = getAccountId();

        var account = accountService.get(accountId);
        if (account != null) {
            return account;
        }

        return getAccountFromToken();
    }

    protected <T> ResponseEntity<T> handle(Command<T> command) {
        try {
            if (command instanceof BaseIdentityCommand<?>) {
                var account = getAccountInfo();

                if (Boolean.TRUE.equals(
                        !isActiveUser(account, command instanceof IRequiredUserValidationCommand
                                && ((IRequiredUserValidationCommand) command).isRequire()))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                ((BaseIdentityCommand<T>) command).setAccountId(account.getId());
                ((BaseIdentityCommand<T>) command).setCompanyId(account.getActiveCompanyId());
            }

            var result = command.execute(pipeline);

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return ResponseEntity.internalServerError().build();
        }
    }

    protected <T> ResponseEntity<?> handle(Command<T> command, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return handleBadRequest(bindingResult);
        }

        return handle(command);

    }

    protected <T> ResponseEntity<?> handleWithResponseMessage(Command<ResponseMessageOf<T>> command,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return handleBadRequest(bindingResult);
        }

        return handleWithResponseMessage(command);

    }

    protected <T> ResponseEntity<?> handleWithResponseMessage(Command<ResponseMessageOf<T>> command) {
        try {

            if (command instanceof BaseIdentityCommand<?>) {
                var account = getAccountInfo();

                if (Boolean.TRUE.equals(
                        !isActiveUser(
                                account, command instanceof IRequiredUserValidationCommand
                                        && ((IRequiredUserValidationCommand) command).isRequire()))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                ((BaseIdentityCommand<ResponseMessageOf<T>>) command).setAccountId(account.getId());
                ((BaseIdentityCommand<ResponseMessageOf<T>>) command).setCompanyId(account.getActiveCompanyId());
            }

            var message = command.execute(pipeline);

            if (message.getSucceeded()) {
                return ResponseEntity.status(message.getStatus()).body(message.getData());

            }

            return ResponseEntity
                    .status(message.getStatus())
                    .body(new ResponseMessageOf<T>(message.getMessage(), message.getFieldErrors()));

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return ResponseEntity.internalServerError().build();
        }

    }

    protected <T> ResponseEntity<?> handleWithResponse(Command<ResponseEntity<T>> command,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return handleBadRequest(bindingResult);
        }

        return handleWithResponse(command);

    }

    protected <T> ResponseEntity<T> handleWithResponse(Command<ResponseEntity<T>> command) {
        try {

            if (command instanceof BaseIdentityCommand<?>) {
                var account = getAccountInfo();

                if (Boolean.TRUE.equals(
                        !isActiveUser(
                                account, command instanceof IRequiredUserValidationCommand
                                        && ((IRequiredUserValidationCommand) command).isRequire()))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                ((BaseIdentityCommand<ResponseEntity<T>>) command).setAccountId(account.getId());
                ((BaseIdentityCommand<ResponseEntity<T>>) command).setCompanyId(account.getActiveCompanyId());
            }

            return command.execute(pipeline);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return ResponseEntity.internalServerError().build();
        }

    }

    private ResponseEntity<?> handleBadRequest(BindingResult bindingResult) {
        var errors = bindingResult.getFieldErrors();

        var errorRequests = new HashMap<String, String>();

        errors.forEach(i -> {
            errorRequests.put(i.getField(), i.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage("invalid data", errorRequests));
    }

    private <T> Boolean isActiveUser(AccountEntity account, boolean isRequiredUserValidation) {

        if (!isRequiredUserValidation && account.getActiveCompanyId() == null) {
            return true;
        }

        var companyUser = companyUserCacheService.get(account.getId(), account.getActiveCompanyId());

        return companyUser != null && companyUser.getStatus() == CompanyUserConstant.ACTIVE_STATUS;
    }
}
