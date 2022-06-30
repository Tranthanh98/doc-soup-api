package logixtek.docsoup.api.features.guest.view;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.guest.view.queries.GuestViewCircleChartImage;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("guest/view")
@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
public class GuestViewController extends BaseController {
    public GuestViewController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/view-circle-chart-by-percent")
    public ResponseEntity<byte[]> viewCircleChartByPercent(@RequestParam Float percent){
        var query = GuestViewCircleChartImage.of(percent);
        return handleWithResponse(query);
    }
}
