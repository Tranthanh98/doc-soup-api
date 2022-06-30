package logixtek.docsoup.api.features.content.mappers;

import logixtek.docsoup.api.features.content.commands.CreateDirectory;
import logixtek.docsoup.api.features.content.responses.AggregateContentViewModel;
import logixtek.docsoup.api.infrastructure.entities.DirectoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DirectoryEntityMapper {
  
    DirectoryEntityMapper INSTANCE = Mappers.getMapper( DirectoryEntityMapper.class );

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy",source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "level",ignore = true)
    DirectoryEntity toEntity(CreateDirectory request);

    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isFile", constant = "false")
    AggregateContentViewModel toViewModel(DirectoryEntity request);
}
