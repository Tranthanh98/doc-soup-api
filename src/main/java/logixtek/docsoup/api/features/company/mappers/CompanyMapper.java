package logixtek.docsoup.api.features.company.mappers;

import logixtek.docsoup.api.features.company.responses.CompanyInfoWithPlanTier;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyInfoWithPlanTier toViewModel(CompanyEntity source);
}
