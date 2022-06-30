package logixtek.docsoup.api.features.share.mappers;

import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileEntityMapper {
  
    FileEntityMapper INSTANCE = Mappers.getMapper( FileEntityMapper.class );

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy",source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "extension", ignore = true)
    @Mapping(target = "docExpiredAt", ignore = true)
    FileEntity toEntity(UploadFileCommand request);

}
