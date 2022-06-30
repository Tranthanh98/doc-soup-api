package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.account.commands.UpdateAccount;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UpdateAccountHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateAccountHandler implements Command.Handler<UpdateAccount, ResponseMessageOf<String>> {

    @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.realm}")
    String realm;
    private  final Keycloak keycloak;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Override
    public ResponseMessageOf<String> handle(UpdateAccount command) {

        var accountOption = accountRepository.findById(command.getAccountId());

        if(accountOption.isEmpty())
        {
            return  ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var account = accountOption.get();

        var usersResource = keycloak.realm(realm).users();
        UserResource user = usersResource.get(command.getAccountId());

        if(user ==null)
        {
            return   ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var userRepresentation = user.toRepresentation ();

        var isChanged = false;

        if(userRepresentation.getFirstName() == null
                || !userRepresentation.getFirstName().equals(command.getFirstName())) {
            isChanged=true;
            userRepresentation.setFirstName(command.getFirstName());
            userRepresentation.setLastName("");
        }

        if(isChanged)
        {
            user.update(userRepresentation);
            account.setLastName("");
            account.setFirstName(command.getFirstName());
            account.setPhone(command.getPhone());
            accountService.update(account);
        }else
        {
            if(command.getPhone() != null && !command.getPhone().equals(account.getPhone()))
            {
                account.setPhone(command.getPhone());
                accountService.update(account);
            }
        }

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
