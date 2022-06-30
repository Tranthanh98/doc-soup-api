package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.queries.ListPageStatisticOfViewerOnLink;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.repositories.PageStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListPageStatisticOfViewerOnLinkHandler")
@AllArgsConstructor
public class ListPageStatisticOfViewerOnLinkHandler implements Command.Handler<ListPageStatisticOfViewerOnLink, Collection<PageStatistic>> {

    private  final PageStatisticRepository pageStatisticRepository;
    @Override
    public Collection<PageStatistic> handle(ListPageStatisticOfViewerOnLink query) {

        var resultOption = pageStatisticRepository.findAllPageStatisticByLinkStatisticId(query.getViewerId());

        if(resultOption.isPresent())
        {
            return  resultOption.get();
        }

        return Collections.emptyList();

    }
}
