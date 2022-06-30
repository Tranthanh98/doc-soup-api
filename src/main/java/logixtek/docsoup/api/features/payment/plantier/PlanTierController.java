package logixtek.docsoup.api.features.payment.plantier;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.payment.plantier.queries.GetCurrentPlanTier;
import logixtek.docsoup.api.features.payment.plantier.queries.ListAllPlanTier;
import logixtek.docsoup.api.features.payment.plantier.responses.PlanTierWithLimitedFeature;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("plan-tier")
public class PlanTierController extends BaseController {
    public PlanTierController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping
    public ResponseEntity<List<PlanTierWithLimitedFeature>> listAllPlanTier(){
        var query = new ListAllPlanTier();
        return handleWithResponse(query);
    }

    @GetMapping("current/features")
    public ResponseEntity<Collection<FeatureFlagEntity>> getCurrentPlanTierFeature(){
        var query = new GetCurrentPlanTier();

        return handleWithResponse(query);
    }
}
