package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListAllFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ListAllFileHandler")
@AllArgsConstructor
public class ListAllFileHandler implements Command.Handler<ListAllFile, ResponseEntity<List<FileEntityWithVisits>>> {

    private final FileRepository fileRepository;
    private  final DirectoryRepository directoryRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<List<FileEntityWithVisits>> handle(ListAllFile query) {

        var directoryOption = directoryRepository.findById(query.getDirectoryId());

        if(directoryOption.isPresent() && permissionService.get(directoryOption.get(),query).canRead()) {

            var fileResult = fileRepository.findAllFileWithVisitsByDirectoryIdAndCompanyId(
                    query.getDirectoryId(), query.getCompanyId());

            if (fileResult.isPresent()) {
                return ResponseEntity.ok(fileResult.get());
            }
        }

        return ResponseEntity.noContent().build();
    }
}
