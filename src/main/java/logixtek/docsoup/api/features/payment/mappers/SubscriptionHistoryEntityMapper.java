package logixtek.docsoup.api.features.payment.mappers;

import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;
import logixtek.docsoup.api.infrastructure.entities.SubscriptionHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionHistoryEntityMapper {
  
    SubscriptionHistoryEntityMapper INSTANCE = Mappers.getMapper( SubscriptionHistoryEntityMapper.class );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "historyDate", ignore = true)
    SubscriptionHistoryEntity toEntity(SubscriptionEntity request);

}
