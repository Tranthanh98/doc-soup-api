package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.view.queries.GetPageThumbnailOfDataroomContent;
import logixtek.docsoup.api.features.share.queries.GetPageThumbnail;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.repositories.LinkStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetPageThumbnailOfDataroomContentHandler")
@AllArgsConstructor
public class GetPageThumbnailOfDataroomContentHandler implements Command.Handler<GetPageThumbnailOfDataroomContent, ResponseEntity<Resource>> {

    private final LinkRepository linkRepository;
    private final LinkStatisticRepository linkStatisticRepository;
    private final DocumentRepository documentRepository;
    private final Pipeline pipeline;

    @Override
    public ResponseEntity<Resource> handle(GetPageThumbnailOfDataroomContent query) {
        if(query.getViewerId() == null && query.getDeviceId() == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var linkOption = linkRepository.findById(query.getLinkId());

        if(!linkOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var viewerOption = linkStatisticRepository.findById(query.getViewerId());

        if(!viewerOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var viewer = viewerOption.get();

        if(!query.getDeviceId().equals(viewer.getDeviceId()) || viewer.getAuthorizedAt() == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var documentOption = documentRepository.findByFileIdWithVersion(query.getFileId());
        if(!documentOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var thumbnailCommand = GetPageThumbnail.of(documentOption.get().getId(), query.getPageNumber());
        return  thumbnailCommand.execute(pipeline);

    }
}
