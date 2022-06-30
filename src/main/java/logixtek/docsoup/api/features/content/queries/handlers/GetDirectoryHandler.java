package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.GetDirectory;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class GetDirectoryHandler implements Command.Handler<GetDirectory, ResponseEntity<List<DirectoryEntity>>> {

    private final DirectoryRepository directoryRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<List<DirectoryEntity>> handle(GetDirectory query) {

        var item = directoryRepository.findById(query.getId());

        if(item.isPresent())
        {
            if(!permissionService.get(item.get(),query).canRead())
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            var subItems = directoryRepository.findAllByParentId(query.getId());

            var result = new ArrayList<DirectoryEntity>();
            result.add(item.get());

            if(subItems.isPresent() && !subItems.get().isEmpty()) {
                result.addAll(subItems.get());
            }

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.notFound().build();
    }

}
