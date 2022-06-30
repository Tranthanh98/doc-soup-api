package logixtek.docsoup.api.features.report;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.report.queires.GetDocumentActivity;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("report")
public class ReportController extends BaseController {
    public ReportController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/document-activity")
    public ResponseEntity<?> getDocumentActivity(@RequestParam Integer top, @RequestParam int dateRecent, @RequestParam String sortDirection){
        var query = new GetDocumentActivity();
        query.setTop(top);
        query.setDateRecent(dateRecent);
        query.setSortDirection(sortDirection);
        return handleWithResponseMessage(query);
    }
}
