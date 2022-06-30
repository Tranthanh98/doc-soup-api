package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.FeatureFlagEntity;
import logixtek.docsoup.api.infrastructure.models.FeatureFlat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlagEntity, Long> {
    Optional<List<FeatureFlat>> findAllByPlanTierIdIn(List<Long> plainTierIds);

    Collection<FeatureFlagEntity> findAllByPlanTierId(Long planTierId);
}
