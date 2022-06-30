package logixtek.docsoup.api.features.link.statistic.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.statistic.commands.UpdateLinkStatisticDownloaded;

import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateLinkStatisticDownloaded")
@RequiredArgsConstructor
public class UpdateLinkStatisticDownloadedHandler implements Command.Handler<UpdateLinkStatisticDownloaded, ResponseEntity<String>> {
    private  final LinkStatisticRepository repository;
    private final LinkRepository linkRepository;
    @Override
    public ResponseEntity<String> handle(UpdateLinkStatisticDownloaded command) {
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
        if(viewer.getDownloaded()) {
            return ResponseEntity.accepted().build();
        }

        viewer.setDownloaded(true);
        repository.saveAndFlush(viewer);

        return ResponseEntity.accepted().build();
    }
}
