package logixtek.docsoup.api.features.company.user.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.user.queries.GetUserTeam;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("GetUserTeamHandler")
public class GetUserTeamHandler implements Command.Handler<GetUserTeam, ResponseEntity<AccountEntity>> {

    private final AccountService accountService;
    private final CompanyUserCacheService companyUserCacheService;

    @Override
    public ResponseEntity<AccountEntity> handle(GetUserTeam query) {

        var companyUser = companyUserCacheService.get(query.getAccountId(),
                query.getCompanyId());

        if(companyUser ==null || !companyUser.getRole().equals(RoleDefinition.C_ADMIN)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        var accountEntity = accountService.get(query.getUserId());

        if(accountEntity == null) {
            ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(accountEntity);

    }
}
