package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.PreviewFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.thirdparty.Impl.GoogleDriveService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;


@Component("PreviewFileHandler")
@AllArgsConstructor
public class PreviewFileHandler implements Command.Handler<PreviewFile, ResponseEntity<InputStreamResource>> {

    private final FileRepository fileRepository;
    private final DocumentRepository documentRepository;
    private final PermissionService permissionService;
    private final GoogleDriveService documentService;
    @Override
    public ResponseEntity<InputStreamResource> handle(PreviewFile query) {

        var item = fileRepository.findById(query.getId());

        if(item.isPresent())
        {
            if(!permissionService.getOfFile(item.get(),query).canRead())
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            var file = item.get();

            var documentOption = documentRepository.findFirstByFileIdAndFileVersionAndRefIdIsNull(file.getId(), file.getVersion());

            if(!documentOption.isPresent()) {
                return  ResponseEntity.notFound().build();
            }

            try{
                var outputStream = OutputStream.nullOutputStream();

                documentService
                        .downloadFile(documentOption.get().getSecureId(), outputStream);

                var inputStream = InputStream.nullInputStream();

                IOUtils.copy(inputStream, outputStream);

                var resource = new InputStreamResource(inputStream);

                var headers = new HttpHeaders();
                headers.set("Content-Disposition", String.format("attachment; filename=download-%s", file.getName()));
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(resource.contentLength())
                        .contentType(MediaType.valueOf("application/pdf"))
                        .body(resource);

            }
            catch (Exception exception){
                return ResponseEntity.internalServerError().build();
            }

        }

        return ResponseEntity.notFound().build();
    }

}
