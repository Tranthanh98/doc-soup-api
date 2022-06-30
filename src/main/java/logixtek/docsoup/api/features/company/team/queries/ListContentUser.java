package logixtek.docsoup.api.features.company.team.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.UserStatisticOfFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListContentUser extends BaseIdentityCommand<ResponseEntity<Collection<UserStatisticOfFile>>> {

    String userId;

    @Min(1)
    Integer numOfRecentDay;

}
