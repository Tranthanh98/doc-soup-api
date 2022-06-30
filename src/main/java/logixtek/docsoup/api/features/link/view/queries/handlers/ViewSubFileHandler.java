package logixtek.docsoup.api.features.link.view.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.view.queries.ViewSubFile;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("ViewSubFileHandler")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ViewSubFileHandler extends BaseViewFileHandler implements Command.Handler<ViewSubFile, ResponseEntity<UUID>> {

    private final DirectoryRepository directoryRepository;

    @Override
    public ResponseEntity<UUID> handle(ViewSubFile query) {

        var validateResult = this.validate(query);

        if(!validateResult.getSucceeded())
        {
            return  ResponseEntity.badRequest().build();
        }

        var content = validateResult.getData();

        if(content.getDirectoryId()==null)
        {
            return  ResponseEntity.badRequest().build();
        }

        var directoryOption = directoryRepository.findById(content.getDirectoryId());

        if (!directoryOption.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        var directory = directoryOption.get();

        if(!content.getDirectoryId().equals(query.getDirectoryId())) {

            var childDirectories = directoryRepository.findAllChildrenOfParent(directory.getId());

            if (!childDirectories.stream().anyMatch(x -> x.equals(query.getDirectoryId()))) {
                return ResponseEntity.notFound().build();
            }
        }

        return  this.getLink(query.getLinkId(),query.getDirectoryId(), query.getFileId());
    }

}
