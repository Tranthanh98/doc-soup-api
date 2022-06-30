package logixtek.docsoup.api.features.company.team.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.TeammateStatistic;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;

@AllArgsConstructor(staticName = "of")
@Data
public class GetListTeammate extends PaginationCommand<ResponseEntity<PageResultOf<TeammateStatistic>>> {

    @Min(1)
    Integer numOfRecentDay;

}
