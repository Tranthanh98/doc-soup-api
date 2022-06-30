package logixtek.docsoup.api.features.company.user.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.CompanyUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListCompanyUser extends BaseIdentityCommand<ResponseEntity<Collection<CompanyUser>>> {

    UUID id;
}
