package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.DownloadFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.FileContentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component("DownloadFileHandler")
@AllArgsConstructor
public class DownloadFileHandler implements Command.Handler<DownloadFile, ResponseEntity<Resource>> {

    private  final FileRepository repository;
    private  final FileContentRepository contentRepository;
    private final PermissionService permissionService;

    private static final Logger logger = LoggerFactory.getLogger(DownloadFileHandler.class);

    @Override
    public ResponseEntity<Resource> handle(DownloadFile query) {

        var fileOption = repository.findById(query.getId());

        if(fileOption.isPresent())
        {
            var item = fileOption.get();
            if(!permissionService.getOfFile(item,query).canRead())
            {
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            try {
                var fileContentOption = contentRepository.findById(query.getId());

                if(fileContentOption.isPresent()){
                    var content = fileContentOption.get().getContent();

                    var length = (int) content.length();

                    var byteData = content.getBytes(1, length);

                    var resource = new InputStreamResource(new ByteArrayInputStream(byteData));

                    var headers = new HttpHeaders();
                    headers.set("Content-Disposition", String.format("attachment; filename=download-%s", item.getName()));
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(byteData.length)
                            .contentType(MediaType.valueOf("application/pdf"))
                            .body(resource);
                }

                return ResponseEntity.badRequest().build();
            }catch (Exception ex) {
                logger.error(ex.getMessage(), ex);

                return ResponseEntity.internalServerError().build();
            }
        }

        return  ResponseEntity.notFound().build();

    }
}
