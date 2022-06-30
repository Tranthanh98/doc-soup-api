package logixtek.docsoup.api.features.administrator.plantier.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.plantier.commands.AdminUpdatePlanTier;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component("AdminUpdatePlanTierHandler")
public class AdminUpdatePlanTierHandler implements Command.Handler<AdminUpdatePlanTier, ResponseMessageOf<String>> {
    private final PlanTierRepository planTierRepository;

    @Override
    public ResponseMessageOf<String> handle(AdminUpdatePlanTier command) {

        var planTierUpdate = planTierRepository.findById(command.getId());

        if(planTierUpdate.isPresent()){
            var newPlanTier = planTierUpdate.get();
            
            newPlanTier.setName(command.getName());
            newPlanTier.setDescription(command.getDescription());

            planTierRepository.saveAndFlush(newPlanTier);

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
