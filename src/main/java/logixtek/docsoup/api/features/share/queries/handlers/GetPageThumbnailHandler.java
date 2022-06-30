package logixtek.docsoup.api.features.share.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.queries.GetPageThumbnail;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component("GetPageThumbnailHandler")
@AllArgsConstructor
public class GetPageThumbnailHandler implements Command.Handler<GetPageThumbnail, ResponseEntity<Resource>> {

    private  final DocumentRepository documentRepository;
    private  final DocumentService documentService;

    private static final Logger logger = LoggerFactory.getLogger(GetPageThumbnailHandler.class);

    @Override
    public ResponseEntity<Resource> handle(GetPageThumbnail query) {

        var documentOption = documentRepository.findById(query.getDocumentId());

        if(!documentOption.isPresent()) {

         return ResponseEntity.notFound().build();

        }

        try {

            var dataResult = documentService.getThumbnail(documentOption.get().getSecureId(),query.getPageNumber());

            if(!dataResult.getSucceeded()) {
                return ResponseEntity.badRequest().build();
            }

            var data = dataResult.getData();

            var length = data.length;

            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Disposition", String.format("attachment; filename="+query.getDocumentId()+ query.getPageNumber()+".jpeg"));

            headers.set(HttpHeaders.CACHE_CONTROL, "max-age=31536000");
            return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(length)
                        .contentType(MediaType.valueOf("image/jpeg"))
                        .body(resource);

            }catch (Exception ex) {
                logger.error(ex.getMessage(), ex);

                return ResponseEntity.internalServerError().build();
            }
        }

}

