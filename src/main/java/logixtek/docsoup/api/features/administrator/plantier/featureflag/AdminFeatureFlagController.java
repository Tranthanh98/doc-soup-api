package logixtek.docsoup.api.features.administrator.plantier.featureflag;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.plantier.featureflag.commands.AdminUpdateFeatureFlag;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("internal/feature-flag")
public class AdminFeatureFlagController extends BaseAdminController {
    public AdminFeatureFlagController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @PutMapping("/{featureFlagId}")
    public ResponseEntity<?> updateFeatureFlag(@Valid @RequestBody AdminUpdateFeatureFlag command, @PathVariable Long featureFlagId){
        command.setFeatureFlagId(featureFlagId);
        return handleWithResponseMessage(command);
    }
}
