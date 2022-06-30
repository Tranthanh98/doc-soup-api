package logixtek.docsoup.api.features.nda;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.nda.queries.DownloadVisitorNda;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("nda")

public class NdaController extends BaseController {
    public NdaController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @GetMapping("download/{viewerId}")
    public ResponseEntity<?> downloadVisitorNda(@PathVariable Long viewerId){
        DownloadVisitorNda query = DownloadVisitorNda.of(viewerId);
        return handleWithResponse(query);
    }
}
