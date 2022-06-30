package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.commands.CreateDirectory;
import logixtek.docsoup.api.features.content.mappers.DirectoryEntityMapper;
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

@Component("CreateDirectoryHandler")
@AllArgsConstructor
@Getter
@Setter
public class CreateDirectoryHandler implements Command.Handler<CreateDirectory, ResponseMessageOf<Long>> {

    private final DirectoryRepository directoryRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseMessageOf<Long> handle(CreateDirectory command) {

        Optional<DirectoryEntity> existingDirectoryOption;
        if(Boolean.TRUE.equals(command.getIsTeam())) {
            existingDirectoryOption =  directoryRepository
                    .findFirstByNameAndParentIdAndCompanyIdAndIsTeamTrue(command.getName(),command.getParentId(),command.getCompanyId());
        } else  {
            existingDirectoryOption =  directoryRepository
                    .findFirstByNameAndAccountIdAndParentIdAndCompanyIdAndIsTeamFalse(command.getName(), command.getAccountId(), command.getParentId(),command.getCompanyId());
        }

        if(existingDirectoryOption.isPresent()){
            return ResponseMessageOf.ofBadRequest("Duplicated name", Map.of(CreateDirectory.Fields.name,"Duplicated name"));
        }

        return  checkAndCreateDirectory(command);

    }

    private  ResponseMessageOf<Long> checkAndCreateDirectory(CreateDirectory command)
    {
        var entity = DirectoryEntityMapper.INSTANCE.toEntity(command);

        if (entity.getParentId() > 0) {

            var parentOption= directoryRepository.findById(command.getParentId());

            if(!parentOption.isPresent())
            {
                return ResponseMessageOf.ofBadRequest("Not found the parent directory",Map.of(CreateDirectory.Fields.parentId,"Not found the parent directory"));
            }

            var parent = parentOption.get();
            if(!parentOption.get().getIsTeam().equals(command.getIsTeam())
                    || !permissionService.get(parent,command).canWrite())
            {
                return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
            }

            var newDirectoryId = directoryRepository.addWithParentId(
                    entity.getParentId(),
                    entity.getName(),
                    entity.getAccountId(),
                    entity.getCompanyId().toString());

            if(newDirectoryId.isPresent())
            {
                return  generateResponse(newDirectoryId.get());
            }

            return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);

        } else {

            var result = directoryRepository.saveAndFlush(entity);

            return  generateResponse(result.getId());
        }
    }

    private static ResponseMessageOf<Long> generateResponse(Long id) {

        if(id > 0)
        {
            return ResponseMessageOf.of(HttpStatus.CREATED, id);
        }

        return ResponseMessageOf.of(HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
