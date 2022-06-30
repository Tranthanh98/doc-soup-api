package logixtek.docsoup.api.features.link.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListPageStatisticOfViewerOnLink extends BaseIdentityCommand<Collection<PageStatistic>> {
    Long viewerId;
    UUID linkId;
}
