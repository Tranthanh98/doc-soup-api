package logixtek.docsoup.api.features.payment.plantier.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@NoArgsConstructor
public class GetCurrentPlanTier extends BaseIdentityCommand<ResponseEntity<Collection<FeatureFlagEntity>>> {
}
