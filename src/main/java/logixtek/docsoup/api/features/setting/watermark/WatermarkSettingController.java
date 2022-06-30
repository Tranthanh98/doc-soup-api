package logixtek.docsoup.api.features.setting.watermark;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.setting.watermark.commands.CreateWatermark;
import logixtek.docsoup.api.features.setting.watermark.commands.UpdateWatermark;
import logixtek.docsoup.api.features.setting.watermark.queries.GetDefaultWatermark;
import logixtek.docsoup.api.features.setting.watermark.queries.GetWatermark;
import logixtek.docsoup.api.features.setting.watermark.queries.GetWatermarkImage;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("setting/watermark/")
public class WatermarkSettingController extends BaseController {

    public WatermarkSettingController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService ) {
        super(pipeline, authenticationManager,accountService);
    }

    @GetMapping("/default")
    public ResponseEntity<?> getDefaultWatermark(){
        return handleWithResponse(new GetDefaultWatermark());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWatermark(@PathVariable long id){
        return handleWithResponse(GetWatermark.of(id));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createWatermark(@Valid @ModelAttribute CreateWatermark command,
                                        BindingResult bindingResult)
    {
        return handleWithResponseMessage(command,bindingResult);
    }


    @PutMapping( consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },path = "/{id}")
    public ResponseEntity<?> updateWatermark(@PathVariable Long id,
                                  @Valid @ModelAttribute UpdateWatermark command,
                                  BindingResult bindingResult)
    {
        command.setId(id);
        return  handleWithResponseMessage(command,bindingResult);
    }

    @GetMapping(path = "/{id}/download")
    public ResponseEntity<?> downloadImage(@PathVariable Long id)  {

        return handleWithResponse(GetWatermarkImage.of(id));
    }
}
