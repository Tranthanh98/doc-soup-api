package logixtek.docsoup.api.features.payment.billinginfo.mappers;

import logixtek.docsoup.api.features.payment.billinginfo.responses.PaymentHistoryViewModel;
import logixtek.docsoup.api.infrastructure.entities.PaymentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PaymentHistoryMapper {
    PaymentHistoryMapper INSTANCE = Mappers.getMapper(PaymentHistoryMapper.class);

    List<PaymentHistoryViewModel> toViewModels(List<PaymentHistoryEntity> entities);
}
