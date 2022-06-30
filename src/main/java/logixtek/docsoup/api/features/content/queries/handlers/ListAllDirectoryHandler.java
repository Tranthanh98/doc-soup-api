package logixtek.docsoup.api.features.content.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.queries.ListAllDirectory;
import logixtek.docsoup.api.features.content.responses.DirectoryViewModel;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("ListAllDirectoryHandler")
@AllArgsConstructor
public class ListAllDirectoryHandler implements Command.Handler<ListAllDirectory, ResponseEntity<DirectoryViewModel>> {

    private final DirectoryRepository directoryRepository;

    @Override
    public ResponseEntity<DirectoryViewModel> handle(ListAllDirectory query) {

        var teamDirectories = directoryRepository
                .findAllByCompanyIdAndIsTeamTrue(query.getCompanyId());

        var directories = directoryRepository
                .findAllByAccountIdAndCompanyIdAndIsTeamFalse(query.getAccountId(),query.getCompanyId());

        var result= new DirectoryViewModel();
        if(teamDirectories.isPresent())
        {
            result.setTeam(teamDirectories.get());
        }

        if(directories.isPresent())
        {
            result.setPrivacy(directories.get());
        }

        return ResponseEntity.ok(result);
    }
}
