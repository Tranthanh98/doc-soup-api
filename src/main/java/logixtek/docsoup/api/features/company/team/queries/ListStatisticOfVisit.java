package logixtek.docsoup.api.features.company.team.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.StatisticVisits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListStatisticOfVisit extends BaseIdentityCommand<ResponseEntity<Collection<StatisticVisits>>> {
    String userId;

    @Min(1)
    Integer numOfRecentDay;
}
