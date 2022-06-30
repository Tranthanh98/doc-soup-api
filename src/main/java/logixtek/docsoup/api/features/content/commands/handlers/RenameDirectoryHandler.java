package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.commands.RenameDirectory;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("RenameDirectory")
@AllArgsConstructor
public class RenameDirectoryHandler implements Command.Handler<RenameDirectory, ResponseMessageOf<String>> {

    private final DirectoryRepository directoryRepository;
    private final PermissionService permissionService;
    @Override
    public ResponseMessageOf<String> handle(RenameDirectory command) {

        var itemResult = directoryRepository.findById(command.getId());

        if(itemResult.isPresent())
        {
            if(!permissionService.get(itemResult.get(),command).canWrite())
            {
                return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            var entity = itemResult.get();

            Optional<DirectoryEntity> isExistDirectoryName;

            if(Boolean.TRUE.equals(entity.getIsTeam())){
                isExistDirectoryName = directoryRepository
                        .findFirstByNameAndParentIdAndCompanyIdAndIsTeamTrue(
                                command.getNewName(),
                                entity.getParentId(),
                                command.getCompanyId());
            }
            else{
                isExistDirectoryName = directoryRepository
                        .findFirstByNameAndAccountIdAndParentIdAndCompanyIdAndIsTeamFalse(
                                command.getNewName(),
                                entity.getAccountId(),
                                entity.getParentId(),
                                command.getCompanyId());

            }

            if(isExistDirectoryName.isPresent()){
                return ResponseMessageOf.ofBadRequest("Duplicated name", Map.of(RenameDirectory.Fields.newName,"Duplicated name"));
            }


            entity.setName(command.getNewName());

            directoryRepository.saveAndFlush(entity);

            return   ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);

    }


}
