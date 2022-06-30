package logixtek.docsoup.api.features.payment.services;

import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionService {
    void updateOrInsert(SubscriptionEntity entity);
    void delete(UUID companyId);
    Optional<SubscriptionEntity> get(UUID companyId);
}
