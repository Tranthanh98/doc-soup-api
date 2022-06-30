package logixtek.docsoup.api.features.account.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.account.mappers.AccountMapper;
import logixtek.docsoup.api.features.account.models.AccountWithCompanyInfo;
import logixtek.docsoup.api.features.account.queries.GetAccount;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetAccountHandler")
@AllArgsConstructor
public class GetAccountHandler implements Command.Handler<GetAccount, ResponseEntity<AccountWithCompanyInfo>> {

    private final AccountService accountService;

    private final CompanyUserCacheService companyUserCacheService;

    @Override
    public ResponseEntity<AccountWithCompanyInfo> handle(GetAccount query) {

       var account = accountService.get(query.getAccountId());

       if(account == null){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }

       var companyUser = companyUserCacheService.get(query.getAccountId(), query.getCompanyId());

       if(companyUser == null){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }

       var accountWithCompanyInfo = AccountMapper.INSTANCE.toViewModel(account);

       accountWithCompanyInfo = AccountMapper.INSTANCE.toViewModel(accountWithCompanyInfo, companyUser);

       return ResponseEntity.ok(accountWithCompanyInfo);
    }
}
