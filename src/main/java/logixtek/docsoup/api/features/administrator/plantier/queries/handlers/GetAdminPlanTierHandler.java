package logixtek.docsoup.api.features.administrator.plantier.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.plantier.mappers.AdminPlanTierMapper;
import logixtek.docsoup.api.features.administrator.plantier.queries.GetAdminPlanTier;
import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierDetail;
import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierViewModel;
import logixtek.docsoup.api.infrastructure.repositories.FeatureFlagRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("GetAdminPlanTierHandler")
public class GetAdminPlanTierHandler implements Command.Handler<GetAdminPlanTier, ResponseEntity<AdminPlanTierDetail>> {

    private final PlanTierRepository planTierRepository;
    private final FeatureFlagRepository featureFlagRepository;

    @Override
    public ResponseEntity<AdminPlanTierDetail> handle(GetAdminPlanTier query) {

        var planTier = planTierRepository.findById(query.getPlanTierId());

        if(planTier.isPresent()){
            var planTierResult = AdminPlanTierMapper.INSTANCE.toViewModel(planTier.get());
            var limitations = featureFlagRepository.findAllByPlanTierId(query.getPlanTierId());

            var result = AdminPlanTierDetail.of(planTierResult, limitations);

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.notFound().build();
    }
}
