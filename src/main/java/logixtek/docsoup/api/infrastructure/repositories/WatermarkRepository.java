package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface WatermarkRepository extends JpaRepository<WatermarkEntity, Long>,
        JpaSpecificationExecutor<WatermarkEntity> {

    Optional<WatermarkEntity> findFirstByAccountIdAndCompanyIdAndIsDefaultIsTrue(String accountId, UUID companyId);
}