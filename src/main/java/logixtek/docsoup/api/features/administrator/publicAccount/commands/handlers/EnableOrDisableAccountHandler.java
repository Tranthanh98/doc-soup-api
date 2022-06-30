package logixtek.docsoup.api.features.administrator.publicAccount.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.administrator.publicAccount.commands.EnableOrDisableAccount;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



@AllArgsConstructor
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component("EnableOrDisableAccountHandler")
public class EnableOrDisableAccountHandler implements Command.Handler<EnableOrDisableAccount, ResponseMessageOf<String>> {

    @Value("${logixtek.docsoup.api.infrastructure.configurations.keycloak.realm}")
    String realm;
    private final Keycloak keycloak;
    private final AccountRepository accountRepository;
    private final String USER_GROUP_NAME= "User";

    @Override
    public ResponseMessageOf<String> handle(EnableOrDisableAccount command) {

        var accountOption = accountRepository.findById(command.getAccountId());

        if(accountOption.isEmpty()){
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var account = accountOption.get();

        var realmResource = keycloak.realm(realm);

        var usersResource =realmResource.users();

        var allGroups = realmResource.groups().groups();

        var user = usersResource.get(command.getAccountId());


        var userGroup = allGroups.stream().filter(role -> role.getName().equals(USER_GROUP_NAME))
                .findAny()
                .orElse(null);

        account.setEnable(command.isEnable());

        accountRepository.saveAndFlush(account);

        if(userGroup !=null && !Strings.isNullOrEmpty(userGroup.getId()))
        {
            if(account.getEnable() && !user.groups().stream().anyMatch(x->x.getId().equals(userGroup.getId())))
            {
                user.joinGroup(userGroup.getId());
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

            if(!account.getEnable() && user.groups().stream().anyMatch(x->x.getId().equals(userGroup.getId())))
            {
                user.leaveGroup(userGroup.getId());
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

        }

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
