package logixtek.docsoup.api.features.administrator.company.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Data
public class AdminListCompany extends BaseAdminIdentityCommand<ResponseEntity<Collection<CompanyEntity>>> {
}
