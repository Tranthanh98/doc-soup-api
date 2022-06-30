package logixtek.docsoup.api.features.payment.plantier.mappers;

import logixtek.docsoup.api.features.payment.plantier.responses.PlanTierWithLimitedFeature;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlanTierMapper {
    PlanTierMapper INSTANCE = Mappers.getMapper(PlanTierMapper.class);

    @Mapping(target = "yearlyDiscount", source = "yearlyDiscount")
    @Mapping(target = "monthlyPlanPaypalId", source = "monthlyPlanPaypalId")
    @Mapping(target = "yearlyPlanPaypalId", source = "yearlyPlanPaypalId")
    @Mapping(target = "monthlyFixedPlanPaypalId", source = "monthlyFixedPlanPaypalId")
    @Mapping(target = "yearlyFixedPlanPaypalId", source = "yearlyFixedPlanPaypalId")
    PlanTierWithLimitedFeature toResponse(PlanTierEntity entity);
}
