package logixtek.docsoup.api.features.payment.billinginfo.mappers;

import logixtek.docsoup.api.features.payment.billinginfo.commands.CreateOrEditBillingInfo;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BillingInfoMapper {
    BillingInfoMapper INSTANCE = Mappers.getMapper(BillingInfoMapper.class);

    CompanyEntity updateToEntity(@MappingTarget CompanyEntity target, CreateOrEditBillingInfo source);
}
