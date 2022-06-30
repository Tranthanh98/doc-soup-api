package logixtek.docsoup.api.features.payment.services.Impl;

import logixtek.docsoup.api.features.payment.mappers.SubscriptionHistoryEntityMapper;
import logixtek.docsoup.api.features.payment.services.SubscriptionService;
import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;
import logixtek.docsoup.api.infrastructure.repositories.SubscriptionEntityRepository;
import logixtek.docsoup.api.infrastructure.repositories.SubscriptionHistoryEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultSubscriptionService implements SubscriptionService {

    private final SubscriptionHistoryEntityRepository subscriptionHistoryEntityRepository;
    private final SubscriptionEntityRepository subscriptionEntityRepository;

    @Override
    public void updateOrInsert(SubscriptionEntity entity) {
        var oldEntity = subscriptionEntityRepository.findById(entity.getCompanyId());
        if(oldEntity.isPresent())
        {
            var historyEntity =
                    SubscriptionHistoryEntityMapper.INSTANCE.toEntity(oldEntity.get());

            subscriptionHistoryEntityRepository.save(historyEntity);
        }

        subscriptionEntityRepository.save(entity);
    }

    @Override
    public void delete(UUID companyId) {
        var entity = subscriptionEntityRepository.findById(companyId);
        if(entity.isPresent())
        {
            var historyEntity =
                    SubscriptionHistoryEntityMapper.INSTANCE.toEntity(entity.get());

            subscriptionHistoryEntityRepository.save(historyEntity);
            subscriptionEntityRepository.delete(entity.get());
        }
    }

    @Override
    public Optional<SubscriptionEntity> get(UUID companyId) {
        return subscriptionEntityRepository.findById(companyId);
    }
}
