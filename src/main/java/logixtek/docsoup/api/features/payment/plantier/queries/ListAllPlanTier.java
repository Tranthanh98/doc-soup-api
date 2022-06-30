package logixtek.docsoup.api.features.payment.plantier.queries;

import logixtek.docsoup.api.features.payment.plantier.responses.PlanTierWithLimitedFeature;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ListAllPlanTier extends BaseIdentityCommand<ResponseEntity<List<PlanTierWithLimitedFeature>>> {
}
