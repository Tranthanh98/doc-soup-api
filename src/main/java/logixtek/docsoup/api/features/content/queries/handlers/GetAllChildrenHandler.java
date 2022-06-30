package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.GetAllChildren;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("GetAllChildrenHandler")
@AllArgsConstructor
public class GetAllChildrenHandler implements Command.Handler<GetAllChildren, ResponseEntity<Collection<DirectoryEntity>>> {
    private final DirectoryRepository directoryRepository;

    @Override
    public ResponseEntity<Collection<DirectoryEntity>> handle(GetAllChildren query) {

        var directoryOption = directoryRepository.findByIdAndAccountId(query.getId(), query.getAccountId());

        if(directoryOption.isPresent()){
            var directory = directoryOption.get();
            if(directory.getCompanyId().equals(query.getCompanyId())){
                var directories = directoryRepository.findAllChildrenByDirectoryId(query.getId());

                return ResponseEntity.ok(directories);
            }
        }
        return ResponseEntity.ok(Collections.emptyList());

    }
}
