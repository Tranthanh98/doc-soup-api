package logixtek.docsoup.api.features.company.team.responses;

import logixtek.docsoup.api.infrastructure.models.StatisticVisits;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor(staticName = "of")
@Data
public class StatisticVisitsResponse implements StatisticVisits {

    LocalDate viewedAt;

    Long visits;

}
