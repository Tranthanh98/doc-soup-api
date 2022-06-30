package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.queries.GetFilePageThumbnail;
import logixtek.docsoup.api.features.share.queries.GetPageThumbnail;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetFilePageThumbnailHandler")
@AllArgsConstructor
public class GetFilePageThumbnailHandler implements Command.Handler<GetFilePageThumbnail, ResponseEntity<Resource>> {

    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;
    private final Pipeline pipeline;

    @Override
    public ResponseEntity<Resource> handle(GetFilePageThumbnail query) {

        Integer version;
        if(query.getVersion() == null){
            version = fileRepository.getVersionByFileId(query.getFileId());
        }
        else{
            version = query.getVersion();
        }

        var documentOption = documentRepository.findFirstByFileIdAndFileVersionAndRefIdIsNull(query.getFileId(), version);
        if(!documentOption.isPresent()) {
            return ResponseEntity.notFound().build();
        }

      var thumbnailCommand = GetPageThumbnail.of(documentOption.get().getId(), query.getPage());
        return  thumbnailCommand.execute(pipeline);

    }
}
