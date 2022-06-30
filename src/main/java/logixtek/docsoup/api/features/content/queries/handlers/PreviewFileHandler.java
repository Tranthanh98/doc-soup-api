package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.PreviewFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component("PreviewFileHandler")
@AllArgsConstructor
public class PreviewFileHandler implements Command.Handler<PreviewFile, ResponseEntity<String>> {

    private final FileRepository fileRepository;
    private  final DocumentRepository documentRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<String> handle(PreviewFile query) {

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

            return ResponseEntity.ok(documentOption.get().getSecureId());
        }

        return ResponseEntity.notFound().build();
    }

}
