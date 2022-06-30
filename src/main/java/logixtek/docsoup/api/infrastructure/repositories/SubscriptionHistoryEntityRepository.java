package logixtek.docsoup.api.infrastructure.repositories;

import logixtek.docsoup.api.infrastructure.entities.SubscriptionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionHistoryEntityRepository extends JpaRepository<SubscriptionHistoryEntity, Long> {
}