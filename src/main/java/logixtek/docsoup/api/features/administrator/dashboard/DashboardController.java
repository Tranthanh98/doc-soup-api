package logixtek.docsoup.api.features.administrator.dashboard;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.dashboard.queries.GetActivities;
import logixtek.docsoup.api.features.administrator.dashboard.queries.GetLinksAndDocuments;
import logixtek.docsoup.api.features.administrator.dashboard.queries.Summary;
import logixtek.docsoup.api.infrastructure.enums.PeriodicalFilter;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("internal/dashboard")
public class DashboardController extends BaseAdminController {
    public DashboardController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping("summary")
    public ResponseEntity<?> getSummary(){
        var query = new Summary();

        return handleWithResponse(query);
    }

    @GetMapping("document-links")
    public ResponseEntity<?> getDocumentsAndLinks(@RequestParam("groupBy") PeriodicalFilter groupBy,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){

        var query = GetLinksAndDocuments.of(groupBy, startDate, endDate);

        return handleWithResponse(query);
    }

    @GetMapping("viewer-activity")
    public ResponseEntity<?> getActivities(@RequestParam("groupBy") PeriodicalFilter groupBy,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  startDate,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  endDate){


        var query = GetActivities.of(groupBy, startDate, endDate);

        return handleWithResponse(query);
    }
}
