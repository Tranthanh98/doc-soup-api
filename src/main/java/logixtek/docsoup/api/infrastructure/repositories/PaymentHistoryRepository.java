package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.PaymentHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, Long> {
    List<PaymentHistoryEntity> findAllByCompanyIdOrderByCreatedDateDesc(UUID companyId);

    Optional<PaymentHistoryEntity> findFirstBySubscriptionPaypalIdOrderByCreatedDateDesc(String subscriptionPayPalId);

    Page<PaymentHistoryEntity> findAllByCompanyId(UUID companyId, Pageable pageable);
}
