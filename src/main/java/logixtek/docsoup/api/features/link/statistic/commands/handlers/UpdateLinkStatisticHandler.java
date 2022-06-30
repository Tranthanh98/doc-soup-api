package logixtek.docsoup.api.features.link.statistic.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.statistic.commands.UpdateLinkStatistic;
import logixtek.docsoup.api.features.link.statistic.dto.StatisticData;
import logixtek.docsoup.api.features.link.statistic.mappers.PageStatisticMapper;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import logixtek.docsoup.api.infrastructure.repositories.PageStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateLinkStatisticHandler")
@RequiredArgsConstructor
public class UpdateLinkStatisticHandler implements Command.Handler<UpdateLinkStatistic, ResponseEntity<String>>{

    private  final LinkStatisticRepository repository;
    private  final PageStatisticRepository pageStatisticRepository;
    private final FileRepository fileRepository;
    private final LinkRepository linkRepository;

    @Override
    public ResponseEntity<String> handle(UpdateLinkStatistic command) {

        if(command.getData()==null || command.getData().isEmpty())
        {
            return ResponseEntity.accepted().build();
        }

        var linkOption = linkRepository.findById(command.getLinkId());

        if(!linkOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var viewerOption = repository.findById(command.getViewerId());
        if(!viewerOption.isPresent())
        {
            return  ResponseEntity.notFound().build();
        }

        var viewer = viewerOption.get();

        if(Boolean.TRUE.equals(!viewer.getIsPreview()) && (viewer.getAuthorizedAt()== null
                || !viewer.getDeviceId().equals(command.getDeviceId())
                || !viewer.getLinkId().toString().equals(command.getLinkId().toString())))
        {
            return  ResponseEntity.badRequest().build();
        }

        var data = command.getData();

        var link = linkOption.get();

        if(link.getDocumentId() != null){
            var fileOption = fileRepository.findById(link.getRefId());

            if(!fileOption.isPresent()){
                return ResponseEntity.notFound().build();
            }

            var file = fileOption.get();

            data.forEach((StatisticData item)->{

                var pageOption = pageStatisticRepository.findFirstByLinkStatisticIdAndPageAndSessionId(command.getViewerId(),
                        item.getPage(),command.getSessionId());

                if(pageOption.isPresent() && file.getVersion().equals( pageOption.get().getVersion()))
                {
                    var pageItem = pageOption.get();
                    if(pageItem.getSessionId().equals(command.getSessionId()))
                    {
                        pageItem.setDuration(item.getDuration());
                        if(pageItem.getVisit() == 0 && item.getDuration() >0)
                        {
                            pageItem.setVisit(1);
                        }

                    }else
                    {

                        if(item.getDuration() > 0)
                        {
                            pageItem.setVisit(pageItem.getVisit()+1);
                        }

                        pageItem.setDuration(pageItem.getDuration() + item.getDuration());
                    }

                    pageStatisticRepository.saveAndFlush(pageItem);

                }else {
                    var entity = PageStatisticMapper.INSTANCE.toEntity(command);
                    entity.setPage(item.getPage());
                    entity.setDuration(item.getDuration());
                    entity.setVersion(file.getVersion());
                    if(item.getDuration() > 0)
                    {
                        entity.setVisit(1);
                    }

                    pageStatisticRepository.saveAndFlush(entity);
                }

            });
        }
        repository.sumDuration(command.getLinkId().toString(),command.getViewerId());

        return  ResponseEntity.accepted().build();

    }


}
