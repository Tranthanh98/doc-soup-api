package logixtek.docsoup.api.features.share.document.commands.mappers;

import logixtek.docsoup.api.infrastructure.entities.DocumentEntity;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.thirdparty.models.DocumentInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentMapper {

    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "refId", ignore = true)
    DocumentEntity toEntity(DocumentInfo request);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "refId", ignore = true)
    @Mapping(target = "docName", source = "givenName")
    DocumentEntity updateDocument(@MappingTarget DocumentEntity target, DocumentInfo source);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileId", source = "id")
    @Mapping(target = "fileVersion", expression = "java(source.getVersion() - 1)")
    DocumentEntity updateDocument(@MappingTarget DocumentEntity target, FileEntity source);
}
