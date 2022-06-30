package logixtek.docsoup.api.features.content.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.content.commands.MoveFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("MoveFileHandler")
@AllArgsConstructor
@Getter
@Setter
public class MoveFileHandler implements Command.Handler<MoveFile, ResponseMessageOf<String>> {

    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;
    private final PermissionService permissionService;

    @Override
    public ResponseMessageOf<String> handle(MoveFile command) {

        var itemResult = fileRepository.findById(command.getId());

        var isExistFileName = fileRepository
                .findByDisplayNameAndDirectoryIdAndCompanyId(
                        itemResult.get().getDisplayName(),
                        command.getNewDirectoryId(),
                        command.getCompanyId());

        if(isExistFileName.isPresent()){
            return ResponseMessageOf.ofBadRequest("Duplicated name",Map.of(MoveFile.Fields.newDirectoryId,"Duplicated name"));
        }

        if(!itemResult.isPresent())
        {
            return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var item = itemResult.get();

        if(!permissionService.getOfFile(item,command).canWrite()) {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var newDirectory = directoryRepository.findById(command.getNewDirectoryId());

        if(!newDirectory.isPresent())
        {
            return  ResponseMessageOf.ofBadRequest("Not found directory",Map.of(MoveFile.Fields.newDirectoryId,"Not found directory"));
        }

        if(!permissionService.get(newDirectory.get(), command).canWrite())
        {
            return ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        var entity = itemResult.get();

        entity.setDirectoryId(command.getNewDirectoryId());
        entity.setAccountId(newDirectory.get().getAccountId());

        var result =  fileRepository.saveAndFlush(entity);

        if(result.getId() > 0 && result.getDirectoryId()==command.getNewDirectoryId())
        {
            return  ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return  ResponseMessageOf.of(HttpStatus.UNPROCESSABLE_ENTITY);

    }


}
