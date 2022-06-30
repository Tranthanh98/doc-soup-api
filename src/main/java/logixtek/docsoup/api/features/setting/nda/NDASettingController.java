package logixtek.docsoup.api.features.setting.nda;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.setting.nda.queries.GetAllNDA;
import logixtek.docsoup.api.features.setting.nda.queries.PreviewNDA;
import logixtek.docsoup.api.features.share.commands.DeleteFile;
import logixtek.docsoup.api.features.share.commands.RenameFile;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("setting/nda")
public class NDASettingController extends BaseController {

    public NDASettingController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService ) {
        super(pipeline, authenticationManager,accountService);
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return handleWithResponse(new GetAllNDA());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> upload(@Valid @ModelAttribute UploadFileCommand command,
                                        BindingResult bindingResult)
    {
        if(!command.getNda())
        {
            return  ResponseEntity.badRequest().build();
        }

        return handleWithResponseMessage(command,bindingResult);
    }


    @DeleteMapping("/{id}" )
    public ResponseEntity<?> delete (@PathVariable Long id)  {

        var command = new DeleteFile();
        command.setId(id);
        command.setNda(true);
        return handleWithResponse(command);
    }

    @GetMapping(path = "/{id}/preview")
    public ResponseEntity<?> preview(@PathVariable Long id)  {

        var command = new PreviewNDA();
        command.setId(id);
        return handleWithResponse(command);
    }

    @PutMapping(path = "/{id}/rename")
    public ResponseEntity<?> rename(@PathVariable Long id, @Valid @RequestBody RenameFile command,
                                     BindingResult bindingResult)  {

        command.setId(id);
        return handleWithResponseMessage(command,bindingResult);
    }
}
