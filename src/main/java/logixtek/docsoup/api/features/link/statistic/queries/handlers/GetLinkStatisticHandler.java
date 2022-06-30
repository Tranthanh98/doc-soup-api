package logixtek.docsoup.api.features.link.statistic.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.statistic.queries.GetLinkStatistic;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("GetLinkStatisticHandler")
@RequiredArgsConstructor
public class GetLinkStatisticHandler  implements Command.Handler<GetLinkStatistic, ResultOf<LinkStatisticEntity>>
{
    private  final LinkStatisticRepository repository;
    @Override
    public ResultOf<LinkStatisticEntity> handle(GetLinkStatistic query) {

        var option =   repository.findFirstByLinkIdAndDeviceId(query.getLinkId(),query.getDeviceId());

        if(option.isPresent())
        {
            return ResultOf.of(option.get());
        }

        return  ResultOf.of(false);
    }
}
