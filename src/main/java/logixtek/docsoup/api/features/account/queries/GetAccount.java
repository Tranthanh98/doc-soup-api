package logixtek.docsoup.api.features.account.queries;

import logixtek.docsoup.api.features.account.models.AccountWithCompanyInfo;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import org.springframework.http.ResponseEntity;

public class GetAccount extends BaseIdentityCommand<ResponseEntity<AccountWithCompanyInfo>> {
}
