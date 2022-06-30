package logixtek.docsoup.api.features.company.user.queries;


import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import logixtek.docsoup.api.infrastructure.models.CompanyUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetUserTeam extends BaseIdentityCommand<ResponseEntity<AccountEntity>> {
    String userId;
}
