package logixtek.docsoup.api.features.administrator.plantier.responses;

import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
public class AdminPlanTierDetail {
    AdminPlanTierViewModel planTier;

    Collection<FeatureFlagEntity> limitations;

}
