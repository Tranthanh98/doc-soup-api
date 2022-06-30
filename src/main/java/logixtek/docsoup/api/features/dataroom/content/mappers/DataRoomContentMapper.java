package logixtek.docsoup.api.features.dataroom.content.mappers;

import logixtek.docsoup.api.features.dataroom.content.commands.AddContent;
import logixtek.docsoup.api.infrastructure.entities.DataRoomContentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataRoomContentMapper {

    DataRoomContentMapper INSTANCE = Mappers.getMapper(DataRoomContentMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "dataRoomId", source = "id")
    @Mapping(target = "directoryId", ignore = true)
    @Mapping(target = "fileId", ignore = true)
    DataRoomContentEntity toEntity(AddContent source);
}
