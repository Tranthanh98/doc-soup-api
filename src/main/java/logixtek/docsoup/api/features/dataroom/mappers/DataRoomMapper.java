package logixtek.docsoup.api.features.dataroom.mappers;

import logixtek.docsoup.api.features.dataroom.commands.Create;
import logixtek.docsoup.api.features.dataroom.commands.UpdateDataRoom;
import logixtek.docsoup.api.infrastructure.entities.DataRoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataRoomMapper {

    DataRoomMapper INSTANCE = Mappers.getMapper(DataRoomMapper.class);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    DataRoomEntity toEntity(Create request);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "accountId")
    @Mapping(target = "modifiedBy", ignore = true)
    DataRoomEntity toEntity(DataRoomEntity request);

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", source = "accountId")
    DataRoomEntity updateEntity(@MappingTarget DataRoomEntity target, UpdateDataRoom source);
}
