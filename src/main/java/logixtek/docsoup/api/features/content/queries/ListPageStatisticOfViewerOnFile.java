package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListPageStatisticOfViewerOnFile extends BaseIdentityCommand<Collection<PageStatistic>>
{
    Long viewerId;
    Long fileId;
}
