package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.queries.GetLinkThumbnailPage;
import logixtek.docsoup.api.features.share.queries.GetPageThumbnail;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetLinkThumbnailPageHandler")
@AllArgsConstructor
public class GetLinkThumbnailPageHandler implements Command.Handler<GetLinkThumbnailPage, ResponseEntity<Resource>> {

    private  final LinkRepository linkRepository;
    private  final Pipeline pipeline;
    @Override
    public ResponseEntity<Resource> handle(GetLinkThumbnailPage query) {
        var linkOption = linkRepository.findById(query.getLinkId());

        if(!linkOption.isPresent())
        {
            return  ResponseEntity.notFound().build();
        }

        var thumbnailCommand = GetPageThumbnail.of(linkOption.get().getDocumentId(),query.getPageNumber());

        return  thumbnailCommand.execute(pipeline);
    }
}
