package logixtek.docsoup.api.features.link.statistic.mappers;

import logixtek.docsoup.api.features.link.statistic.commands.UpdateLinkStatistic;
import logixtek.docsoup.api.infrastructure.entities.PageStatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PageStatisticMapper {
    PageStatisticMapper INSTANCE = Mappers.getMapper( PageStatisticMapper.class );

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "page",ignore = true)
    @Mapping(target = "visit",ignore = true)
    @Mapping(target = "duration",ignore = true)
    @Mapping(target = "linkStatisticId",source = "viewerId")
    @Mapping(target = "modifiedDate",expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    PageStatisticEntity toEntity(UpdateLinkStatistic request);
}
