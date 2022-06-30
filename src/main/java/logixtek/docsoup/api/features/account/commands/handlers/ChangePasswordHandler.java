package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.account.commands.ChangePassword;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ChangePasswordHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ChangePasswordHandler implements Command.Handler<ChangePassword, ResponseMessageOf<String>> {
    @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.realm}")
    String realm;
    private  final Keycloak keycloak;

    @Override
    public ResponseMessageOf<String> handle(ChangePassword command) {

        if(!command.getNewPass().equals(command.getConfirmNewPass()))
        {
            return  ResponseMessageOf.ofBadRequest("The confirm password is not matched",
                    Map.of(ChangePassword.Fields.confirmNewPass,"The confirm password is not matched"));
        }

        var usersResource = keycloak.realm(realm).users();
        UserResource user = usersResource.get(command.getAccountId());

        if(user ==null)
        {
            return   ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(command.getNewPass());

        user.resetPassword(credentialRepresentation);

        return  ResponseMessageOf.of(HttpStatus.ACCEPTED);

    }
}
