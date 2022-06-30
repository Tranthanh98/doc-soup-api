package logixtek.docsoup.api.features.link.view.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.commands.ValidateViewRequest;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ValidateViewRequestHandler")
@AllArgsConstructor
public class ValidateViewRequestHandler implements Command.Handler<ValidateViewRequest, ResultOf<DataRoomContentEntity>> {

    private  final LinkRepository linkRepository;
    private  final LinkStatisticRepository linkStatisticRepository;
    private  final DataRoomContentRepository dataRoomContentRepository;
    @Override
    public ResultOf<DataRoomContentEntity> handle(ValidateViewRequest query) {
        var linkOption = linkRepository.findById(query.getLinkId());

        if(!linkOption.isPresent())
        {
            return ResultOf.of(false);
        }

        var link = linkOption.get();

        var viewerSessionOption = linkStatisticRepository.findById(query.getViewerId());

        if(!viewerSessionOption.isPresent())
        {
            return  ResultOf.of(false);
        }

        var viewerSession= viewerSessionOption.get();

        if(!viewerSession.getDeviceId().equals(query.getDeviceId()) || !viewerSession.getLinkId().toString().equals(query.getLinkId().toString()))
        {
            return ResultOf.of(false);
        }

        var contentOption = dataRoomContentRepository.findById(query.getContentId());

        if(!contentOption.isPresent())
        {
            return ResultOf.of(false);
        }

        var content = contentOption.get();

        if(content.getDataRoomId()!= link.getRefId())
        {
            return ResultOf.of(false);
        }

        return  ResultOf.of(content);
    }
}
