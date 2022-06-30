package logixtek.docsoup.api.features.link.mappers;

import logixtek.docsoup.api.features.link.commands.CreateLink;
import logixtek.docsoup.api.features.link.commands.UpdateLinkSetting;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkMapper {

    LinkMapper INSTANCE = Mappers.getMapper(LinkMapper.class);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy",source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "refId", source = "resourceId")
    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    LinkEntity toEntity(CreateLink request);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", source = "accountId")
    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    LinkEntity updateLinkEntity(@MappingTarget LinkEntity target, UpdateLinkSetting request);


}
