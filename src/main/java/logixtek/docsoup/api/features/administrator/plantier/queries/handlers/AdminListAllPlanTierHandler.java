package logixtek.docsoup.api.features.administrator.plantier.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.plantier.mappers.AdminPlanTierMapper;
import logixtek.docsoup.api.features.administrator.plantier.queries.AdminListAllPlanTier;
import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierViewModel;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("AdminListAllPlanTierHandler")
@AllArgsConstructor
public class AdminListAllPlanTierHandler implements Command.Handler<AdminListAllPlanTier, ResponseEntity<Collection<AdminPlanTierViewModel>>> {

    private final PlanTierRepository planTierRepository;

    @Override
    public ResponseEntity<Collection<AdminPlanTierViewModel>> handle(AdminListAllPlanTier query) {
        var planTiers = planTierRepository.findAllByOrderByLevel();

        var response = AdminPlanTierMapper.INSTANCE.toViewModel(planTiers);

        return ResponseEntity.ok(response);
    }
}
