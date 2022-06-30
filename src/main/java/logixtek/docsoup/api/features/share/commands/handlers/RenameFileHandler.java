package logixtek.docsoup.api.features.share.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.share.commands.RenameFile;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component("RenameFileHandler")
@AllArgsConstructor
public class RenameFileHandler implements Command.Handler<RenameFile, ResponseMessageOf<String>> {

    private final FileRepository repository;
    private final PermissionService permissionService;
    @Override
    public ResponseMessageOf<String> handle(RenameFile command) {

        var itemResult = repository.findById(command.getId());

        if(!itemResult.isPresent()) {
            return  ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        var item = itemResult.get();

        if(!Objects.equals(item.getNda(), command.getNda())) {
            return ResponseMessageOf.ofBadRequest("Wrong document type", Map.of(RenameFile.Fields.nda,"Wrong document type."));
        }

        if(!permissionService.getOfFile(item,command).canWrite())
        {
            return  ResponseMessageOf.of(HttpStatus.FORBIDDEN);
        }

        if(command.getNewName().equals(item.getDisplayName()))
        {
            return  ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var isExistFileName = repository.findByDisplayNameAndDirectoryIdAndCompanyId(command.getNewName(),item.getDirectoryId(),item.getCompanyId());

        if(isExistFileName.isPresent()){
            return ResponseMessageOf.ofBadRequest("Duplicated name", Map.of(RenameFile.Fields.newName,"The file name has already taken."));
        }

            item.setDisplayName(command.getNewName());

            repository.saveAndFlush(item);

            return  ResponseMessageOf.of(HttpStatus.ACCEPTED);


    }


}
