package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlanTierRepository extends JpaRepository<PlanTierEntity, Long> {
    Optional<PlanTierEntity> findByLevelAndIsActiveIsTrue(Long level);
    Optional<PlanTierEntity> findByMonthlyPlanPaypalIdOrMonthlyFixedPlanPaypalIdAndIsActiveIsTrue(String monthlyPlanPaypalId, String monthlyFixedPlanPaypalId);
    Optional<PlanTierEntity> findByYearlyPlanPaypalIdOrYearlyFixedPlanPaypalIdAndIsActiveIsTrue(String yearlyPlanPaypalId, String yearlyFixedPlanPaypalId);

    Collection<PlanTierEntity> findAllByOrderByLevel();

    Optional<PlanTierEntity> findByIdAndIsActiveIsTrue(Long planTierId);

    List<PlanTierEntity> findAllByIsActiveIsTrue();
}
