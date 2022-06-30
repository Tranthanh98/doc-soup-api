package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.queries.GetAllChildren;
import logixtek.docsoup.api.features.link.view.queries.AllChildrenDirectory;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("AllChildrenDirectoryHandler")
@AllArgsConstructor
public class AllChildrenDirectoryHandler implements Command.Handler<AllChildrenDirectory, ResponseEntity<Collection<DirectoryEntity>>> {
    private final LinkStatisticRepository linkStatisticRepository;
    private final Pipeline pipeline;
    private final DirectoryRepository directoryRepository;

    @Override
    public ResponseEntity<Collection<DirectoryEntity>> handle(AllChildrenDirectory query) {
        var viewerOptions = linkStatisticRepository.findById(query.getViewerId());

        if(!viewerOptions.isPresent()){
            return ResponseEntity.ok(Collections.emptyList());
        }

        var viewer = viewerOptions.get();

        if(!viewer.getLinkId().equals(query.getLinkId()) || !viewer.getDeviceId().equals(query.getDeviceId())){
            return ResponseEntity.ok(Collections.emptyList());
        }

        var directories = directoryRepository.findAllChildrenByDirectoryId(query.getDirectoryId());

        return ResponseEntity.ok(directories);
    }
}
