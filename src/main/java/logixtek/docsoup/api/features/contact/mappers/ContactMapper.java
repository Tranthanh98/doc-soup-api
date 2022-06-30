package logixtek.docsoup.api.features.contact.mappers;

import logixtek.docsoup.api.features.contact.commands.CreateContact;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy",source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "archived", ignore = true)
    ContactEntity toEntity(CreateContact request);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", source = "accountId")
    @Mapping(target = "archived", ignore = true)
    ContactEntity toEntity(@MappingTarget ContactEntity target, CreateContact source);
}
