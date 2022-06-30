package logixtek.docsoup.api.features.link.linkAccount.mappers;

import logixtek.docsoup.api.features.link.linkAccount.commands.CreateLinkAccount;
import logixtek.docsoup.api.infrastructure.entities.LinkAccountsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkAccountMapper {
    LinkAccountMapper INSTANCE = Mappers.getMapper(LinkAccountMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy",source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    LinkAccountsEntity toEntity(CreateLinkAccount entity);
}
