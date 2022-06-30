package logixtek.docsoup.api.features.payment.plantier.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.plantier.mappers.PlanTierMapper;
import logixtek.docsoup.api.features.payment.plantier.queries.ListAllPlanTier;
import logixtek.docsoup.api.features.payment.plantier.responses.PlanTierWithLimitedFeature;
import logixtek.docsoup.api.infrastructure.repositories.FeatureFlagRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("ListAllPlanTierHandler")
@AllArgsConstructor
public class ListAllPlanTierHandler implements Command.Handler<ListAllPlanTier, ResponseEntity<List<PlanTierWithLimitedFeature>>> {
    private final PlanTierRepository planTierRepository;
    private final FeatureFlagRepository featureFlatRepository;

    @Override
    public ResponseEntity<List<PlanTierWithLimitedFeature>> handle(ListAllPlanTier query) {
        var planTiers = planTierRepository.findAllByIsActiveIsTrue();

        var result = planTiers.stream().map(item -> PlanTierMapper.INSTANCE.toResponse(item)).collect(Collectors.toList());

        var planTierIds = result.stream().map(PlanTierWithLimitedFeature::getId).collect(Collectors.toList());

        var allLimitedFeaturesOption = featureFlatRepository.findAllByPlanTierIdIn(planTierIds);

        if(!allLimitedFeaturesOption.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        var allLimitedFeatures = allLimitedFeaturesOption.get();

        result.forEach(item -> {
            var limits = allLimitedFeatures.stream().filter(x -> item.getId().equals(x.getPlanTierId())).collect(Collectors.toList());
            item.setLimits(limits);
        });

        return ResponseEntity.ok(result);
    }
}
