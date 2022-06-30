package logixtek.docsoup.api.features.setting.watermark.mappers;

import logixtek.docsoup.api.features.setting.watermark.commands.CreateWatermark;
import logixtek.docsoup.api.infrastructure.entities.WatermarkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WatermarkMapper {

    WatermarkMapper INSTANCE = Mappers.getMapper(WatermarkMapper.class);

    @Mapping(target = "createdDate",ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdBy", source = "accountId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "imageType",ignore = true)
    WatermarkEntity toEntity(CreateWatermark request);

}
