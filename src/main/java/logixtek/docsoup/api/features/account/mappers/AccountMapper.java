package logixtek.docsoup.api.features.account.mappers;

import logixtek.docsoup.api.features.account.models.AccountWithCompanyInfo;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountWithCompanyInfo toViewModel(AccountEntity source);

    @Mapping(source = "member_type", target = "member")
    AccountWithCompanyInfo toViewModel(@MappingTarget AccountWithCompanyInfo target, CompanyUserEntity source);
}
