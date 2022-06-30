package logixtek.docsoup.api.features.link.statistic.queries;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetLinkStatistic implements Command<ResultOf<LinkStatisticEntity>> {

    String deviceId;

    UUID linkId;
}
