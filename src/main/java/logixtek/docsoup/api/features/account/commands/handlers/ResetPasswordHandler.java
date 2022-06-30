package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import logixtek.docsoup.api.features.account.commands.ResetPassword;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("ResetPasswordHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResetPasswordHandler implements Command.Handler<ResetPassword, ResponseMessageOf<String>> {

    @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.realm}")
    String realm;

    private  final Keycloak keycloak;

    private final AccountRepository accountRepository;

    private final EncryptService encryptService;

    @SneakyThrows
    @Override
    public ResponseMessageOf<String> handle(ResetPassword command) {

        if(!command.getPassword().equals(command.getConfirmPassword())){
            return ResponseMessageOf.ofBadRequest(ResponseResource.PasswordNotMatch,
                    Map.of(ResetPassword.Fields.confirmPassword, ResponseResource.PasswordNotMatch));
        }

        var decryptString = encryptService.decrypt(command.getToken());

        Map<String, Object> resetPasswordInfo = new ObjectMapper().readValue(decryptString, HashMap.class);
        if(resetPasswordInfo.isEmpty()) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidResetPassword,
                    Map.of(ResetPassword.Fields.token, ResponseResource.InvalidToken));
        }

        var accountId = resetPasswordInfo.get("accountId").toString();

        var accountOption = accountRepository.findById(accountId);

        if(!accountOption.isPresent()){
            return new ResponseMessageOf<>(HttpStatus.NOT_FOUND, ResponseResource.NotFoundUser);
        }

        var account = accountOption.get();

        if(account.getToken() == null || !account.getToken().equals(command.getToken())){
            return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidToken, Map.of());
        }

        var usersResource = keycloak.realm(realm).users();
        UserResource user = usersResource.get(accountId);

        if(user == null)
        {
            return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(command.getPassword());

        user.resetPassword(credentialRepresentation);

        account.setToken(null);

        accountRepository.saveAndFlush(account);

        return  ResponseMessageOf.of(HttpStatus.ACCEPTED);

    }
}
