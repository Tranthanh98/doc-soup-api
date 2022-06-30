package logixtek.docsoup.api.features.guest.download;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.guest.download.queries.GuestDownloadFile;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("guest/download")
@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
public class GuestDownloadController extends BaseController {
    public GuestDownloadController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/{fileType}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileType, @RequestParam String resourceId){
        var query = GuestDownloadFile.of(fileType, resourceId);
        return handleWithResponse(query);
    }
}
