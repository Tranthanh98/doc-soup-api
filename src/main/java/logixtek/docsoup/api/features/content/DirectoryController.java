package logixtek.docsoup.api.features.content;

import an.awesome.pipelinr.Pipeline;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import logixtek.docsoup.api.features.content.commands.CreateDirectory;
import logixtek.docsoup.api.features.content.commands.DeleteDirectory;
import logixtek.docsoup.api.features.content.commands.MoveDirectory;
import logixtek.docsoup.api.features.content.commands.RenameDirectory;
import logixtek.docsoup.api.features.content.queries.*;
import logixtek.docsoup.api.features.content.responses.DirectoryViewModel;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("directory")
@Api( tags = "directory")
public class DirectoryController extends BaseController {

    public DirectoryController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping()
    @ApiOperation(value = "This method is used to get all directory.")
    public ResponseEntity<DirectoryViewModel> listAllDirectory()
    {
        return handleWithResponse(new ListAllDirectory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<DirectoryEntity>> getDirectory(@PathVariable Long id){
        var query = GetDirectory.of(id);
        return handleWithResponse(query);
    }

    @PostMapping()
    public ResponseEntity<?> createDirectory(@Valid @RequestBody CreateDirectory create,BindingResult bindingResult){
        return handleWithResponseMessage(create,bindingResult);
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<?> renameDirectory(@PathVariable Long id,
                                                  @Valid @RequestBody RenameDirectory command,
                                                  BindingResult bindingResult)
    {
        command.setId(id);
        return  handleWithResponseMessage(command, bindingResult);
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<?> move(@PathVariable Long id,
                                       @Valid @RequestBody MoveDirectory command,
                                       BindingResult bindingResult)
    {
        command.setId(id);
        return  handleWithResponseMessage(command,bindingResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDirectory(@PathVariable Long id){
        var command = DeleteDirectory.of(id);
        return handleWithResponseMessage(command);
    }

    @GetMapping("/{id}/aggregate-content")
    public ResponseEntity<?> aggregateContent(@PathVariable Long id){
        var query = new ListAllDirectoryAndFile();
        query.setDirectoryId(id);

        return handleWithResponseMessage(query);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDirectories(@Valid SearchDirectory query, BindingResult bindingResult){
        return handleWithResponse(query, bindingResult);
    }

    @GetMapping("/{id}/all-children")
    public ResponseEntity<?> allChildren(@PathVariable Long id){
        var query = GetAllChildren.of(id);
        return handleWithResponse(query);
    }
}
