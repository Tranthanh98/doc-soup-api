package logixtek.docsoup.api.features.company.team;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.company.team.queries.GetListTeammate;
import logixtek.docsoup.api.features.company.team.queries.ListContentUser;
import logixtek.docsoup.api.features.company.team.queries.ListStatisticOfVisit;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("teams")
public class TeamController extends BaseController {
    public TeamController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService) {
        super(pipeline, authenticationManager, accountService);
    }

    @GetMapping
    public ResponseEntity<?> getListTeammate(@QueryParam Integer numOfRecentDay,
                                             @QueryParam Integer page,
                                             @QueryParam Integer pageSize) {
        var query = GetListTeammate.of(numOfRecentDay);
        query.setPage(page);
        query.setPageSize(pageSize);

        return handleWithResponse(query);
    }

    @GetMapping("/content/user/{userId}")
    public ResponseEntity<?> getListContentByUserId(@PathVariable String userId,
                                                    @QueryParam Integer numOfRecentDay
                                                    ){
        var query = ListContentUser.of(userId, numOfRecentDay);
        return handleWithResponse(query);
    }

    @GetMapping("/visit/statistic/{userId}")
    public ResponseEntity<?> getListStatisticOfVisitByDate(@PathVariable String userId,
                                                           @QueryParam Integer numOfRecentDay
                                                           ){
        var query = ListStatisticOfVisit.of(userId, numOfRecentDay);
        return handleWithResponse(query);
    }
}
