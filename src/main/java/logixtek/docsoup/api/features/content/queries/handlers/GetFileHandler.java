package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.GetFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class GetFileHandler implements Command.Handler<GetFile, ResponseEntity<FileEntity>> {

    private final FileRepository fileRepository;
    private PermissionService permissionService;
    @Override
    public ResponseEntity<FileEntity> handle(GetFile query) {

        var item = fileRepository.findById(query.getId());

        if(item.isPresent())
        {
            if(!permissionService.getOfFile(item.get(),query).canRead()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(item.get());
        }

        return ResponseEntity.notFound().build();
    }

}
