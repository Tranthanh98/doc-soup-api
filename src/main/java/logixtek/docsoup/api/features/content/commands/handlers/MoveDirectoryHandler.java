package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.commands.MoveDirectory;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("MoveDirectory")
@AllArgsConstructor
@Getter
@Setter
public class MoveDirectoryHandler implements Command.Handler<MoveDirectory, ResponseMessageOf<String>> {

    private final DirectoryRepository directoryRepository;
    private final PermissionService permissionService;
    @Override
    public ResponseMessageOf<String> handle(MoveDirectory command) {

        var itemResult = directoryRepository.findById(command.getId());

        if(itemResult.isPresent())
        {
            if(!permissionService.get(itemResult.get(),command).canWrite())
            {
                return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            if(command.getNewParentId()>0) {

                var newParentOption = directoryRepository.findById(command.getNewParentId());
                if(!newParentOption.isPresent())
                {
                    return ResponseMessageOf.ofBadRequest("Not found the parent directory", Map.of(MoveDirectory.Fields.newParentId,"Not found the parent directory"));
                }

                if(!permissionService.get(newParentOption.get(),command).canWrite() || !newParentOption.get().getIsTeam().equals(command.getIsTeam()))
                {
                    return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
                }

            }

            var allChildrenOfParent =
                    directoryRepository.findAllChildrenOfParent(command.getId());

            if(!allChildrenOfParent.isEmpty()){
                if(allChildrenOfParent.stream().anyMatch(i -> i.equals(command.getNewParentId()))){
                    return ResponseMessageOf.ofBadRequest("Invalid request",Map.of(MoveDirectory.Fields.newParentId,"Invalid request"));
                }
            }

            Optional<DirectoryEntity> isExistDirectoryName;

            if(Boolean.TRUE.equals(command.getIsTeam())){
                isExistDirectoryName = directoryRepository
                        .findFirstByNameAndParentIdAndCompanyIdAndIsTeamTrue(
                                itemResult.get().getName(),
                                command.getNewParentId(),
                                command.getCompanyId());
            }
            else {
                isExistDirectoryName = directoryRepository
                        .findFirstByNameAndAccountIdAndParentIdAndCompanyIdAndIsTeamFalse(
                                itemResult.get().getName(),
                                command.getAccountId(),
                                command.getNewParentId(),
                                command.getCompanyId());
            }


            if(isExistDirectoryName.isPresent() && !isExistDirectoryName.isEmpty()){
                return  ResponseMessageOf.ofBadRequest("Duplicated directory name on the new parent directory",
                        Map.of(MoveDirectory.Fields.newParentId,"Duplicated directory name on the new parent directory"));
            }

            var moveResult= directoryRepository.move(command.getNewParentId(),
                    command.getId(),
                    command.getCompanyId().toString(),
                    command.getAccountId(),
                    command.getIsTeam());

            if(moveResult.isPresent())
            {
                return ResponseMessageOf.of(HttpStatus.valueOf(moveResult.get()));
            }

            return  ResponseMessageOf.of(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);

    }


}
