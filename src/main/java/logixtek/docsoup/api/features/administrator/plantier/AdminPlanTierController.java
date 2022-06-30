package logixtek.docsoup.api.features.administrator.plantier;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.plantier.commands.AdminUpdatePlanTier;
import logixtek.docsoup.api.features.administrator.plantier.commands.EnableOrDisablePlanTier;
import logixtek.docsoup.api.features.administrator.plantier.queries.AdminListAllPlanTier;
import logixtek.docsoup.api.features.administrator.plantier.queries.GetAdminPlanTier;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("internal/plan-tiers")
public class AdminPlanTierController extends BaseAdminController {
    public AdminPlanTierController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping
    public ResponseEntity<?> listAllPLanTiers(){
        return handleWithResponse(new AdminListAllPlanTier());
    }

    @PutMapping("/{planTierId}")
    public ResponseEntity<?> enableOrDisablePlanTier(@Valid @RequestBody EnableOrDisablePlanTier command,
                                                     @PathVariable Long planTierId){

        command.setPlanTierId(planTierId);

        return handleWithResponseMessage(command);
    }

    @GetMapping("/{planTierId}")
    public ResponseEntity<?> getPlanTierById(@PathVariable Long planTierId){

        return handleWithResponse(GetAdminPlanTier.of(planTierId));
    }

    @PutMapping
    public ResponseEntity<?> updatePlanTier(@Valid @RequestBody AdminUpdatePlanTier command){

        return handleWithResponseMessage(command);
    }
}
