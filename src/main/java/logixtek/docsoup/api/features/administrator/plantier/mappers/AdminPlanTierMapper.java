package logixtek.docsoup.api.features.administrator.plantier.mappers;

import logixtek.docsoup.api.features.administrator.plantier.responses.AdminPlanTierViewModel;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

@Mapper
public interface AdminPlanTierMapper {
    AdminPlanTierMapper INSTANCE = Mappers.getMapper(AdminPlanTierMapper.class);

    Collection<AdminPlanTierViewModel> toViewModel(Collection<PlanTierEntity> source);

    AdminPlanTierViewModel toViewModel(PlanTierEntity source);
}
