package logixtek.docsoup.api.infrastructure.controllers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessage;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('internal_site')")
public class BaseAdminController {
    private final Pipeline pipeline;

    protected final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    protected String getInternalAccountId() {
        var token = (Jwt) this.authenticationManager.getAuthentication().getPrincipal();

        return token.getClaim("sub");
    }

    protected String getAccountEmailFromToken(){
        var token = (Jwt) this.authenticationManager.getAuthentication().getPrincipal();

        return token.getClaim("email");
    }

    protected <T> ResponseEntity<T> handle(Command<T> command) {
        try
        {
            if(command instanceof BaseAdminIdentityCommand<?>)
            {
                var internalAccountId = getInternalAccountId();

                ((BaseAdminIdentityCommand<T>) command).setInternalAccountId(internalAccountId);
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

            if(command instanceof BaseAdminIdentityCommand<?>)
            {
                var internalAccountId = getInternalAccountId();

                ((BaseAdminIdentityCommand<T>) command).setInternalAccountId(internalAccountId);
            }

            var message= command.execute(pipeline);

            if(message.getSucceeded())
            {
                return ResponseEntity.status(message.getStatus()).body(message.getData());

            }

            return ResponseEntity
                    .status(message.getStatus())
                    .body(new ResponseMessageOf<T>(message.getMessage() , message.getFieldErrors()));

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

            if(command instanceof BaseAdminIdentityCommand<?>)
            {
                var internalAccountId = getInternalAccountId();

                ((BaseAdminIdentityCommand<T>) command).setInternalAccountId(internalAccountId);
            }

            return command.execute(pipeline);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return ResponseEntity.internalServerError().build();
        }

    }

    private ResponseEntity<?> handleBadRequest(BindingResult bindingResult){
        var errors = bindingResult.getFieldErrors();

        var errorRequests = new HashMap<String, String>();

        errors.forEach(i -> {
            errorRequests.put(i.getField(), i.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage("invalid data" , errorRequests));
    }
}
