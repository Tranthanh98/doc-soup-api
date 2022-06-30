package logixtek.docsoup.api.features.company.user.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.CompanyOfUser;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public class GetListCompanyOfUser extends BaseIdentityCommand<ResponseEntity<Collection<CompanyOfUser>>> {
}
