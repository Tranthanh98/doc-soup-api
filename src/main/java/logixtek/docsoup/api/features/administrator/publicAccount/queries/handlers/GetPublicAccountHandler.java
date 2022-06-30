package logixtek.docsoup.api.features.administrator.publicAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.publicAccount.queries.GetPublicAccount;
import logixtek.docsoup.api.features.administrator.publicAccount.responses.PublicAccountDetail;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetPublicAccountHandler")
@AllArgsConstructor
public class GetPublicAccountHandler implements Command.Handler<GetPublicAccount, ResponseEntity<PublicAccountDetail>> {

    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    @Override
    public ResponseEntity<PublicAccountDetail> handle(GetPublicAccount query) {


        var accountDetails = accountRepository.findDetailedAccountById(query.getAccountId());

        if(accountDetails.isPresent()){
            var companiesOfUser = companyRepository.getUserCompanyWithPlanTier(query.getAccountId());
            var result = PublicAccountDetail.of(accountDetails.get(), companiesOfUser.get());

            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }
}
