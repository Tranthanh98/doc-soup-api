package logixtek.docsoup.api.features.administrator.plantier.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.plantier.commands.EnableOrDisablePlanTier;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component("EnableOrDisablePlanTierHandler")
public class EnableOrDisablePlanTierHandler implements Command.Handler<EnableOrDisablePlanTier, ResponseMessageOf<String>> {

    private final PlanTierRepository planTierRepository;
    private final CompanyRepository companyRepository;

    @Override
    public ResponseMessageOf<String> handle(EnableOrDisablePlanTier command) {

        if(!command.getIsActive()){
            var numberOfActivePlanTierCompany  = companyRepository.countByPlanTierId(command.getPlanTierId());

            if(numberOfActivePlanTierCompany  > 0){
                return ResponseMessageOf.ofBadRequest(ResponseResource.SomeCompaniesUsingPlanTier,
                        Map.of(EnableOrDisablePlanTier.Fields.planTierId, ResponseResource.SomeCompaniesUsingPlanTier));
            }
        }

        var planTier = planTierRepository.findById(command.getPlanTierId());

        if(planTier.isEmpty()){
            return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
        }

        if(planTier.get().getIsActive()== command.getIsActive()){
            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        var newPlanTier = planTier.get();

        newPlanTier.setIsActive(command.getIsActive());
        planTierRepository.saveAndFlush(newPlanTier);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);

    }
}
