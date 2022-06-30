package logixtek.docsoup.api.features.payment.plantier.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.plantier.queries.GetCurrentPlanTier;
import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.FeatureFlagRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("GetCurrentPlanTierHandler")
@AllArgsConstructor
public class GetCurrentPlanTierHandler implements Command.Handler<GetCurrentPlanTier, ResponseEntity<Collection<FeatureFlagEntity>>> {

    private final FeatureFlagRepository featureFlagRepository;
    private final CompanyRepository companyRepository;

    @Override
    public ResponseEntity<Collection<FeatureFlagEntity>> handle(GetCurrentPlanTier query) {
        var companyOption = companyRepository.findById(query.getCompanyId());
        if(!companyOption.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        var company = companyOption.get();

        var features = featureFlagRepository.findAllByPlanTierId(company.getPlanTierId());

        return ResponseEntity.ok(features);
    }
}
