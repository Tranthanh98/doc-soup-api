package logixtek.docsoup.api.features.link.statistic.mappers;

import logixtek.docsoup.api.features.link.statistic.commands.CreateLinkStatistic;
import logixtek.docsoup.api.infrastructure.entities.LinkStatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LinkStatisticEntityMapper {

    LinkStatisticEntityMapper INSTANCE = Mappers.getMapper( LinkStatisticEntityMapper.class );

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "visit", expression = "java(Long.valueOf(1))")
    @Mapping(target = "duration",expression = "java(Long.valueOf(1))")
    @Mapping(target = "lastPage",expression = "java(Integer.valueOf(1))")
    @Mapping(target = "totalPage",expression = "java(Integer.valueOf(1))")
    @Mapping(target = "contactId",ignore = true)
    @Mapping(target = "location",ignore = true)
    @Mapping(target = "deviceName",ignore = true)
    @Mapping(target = "NDAToken",ignore = true)
    @Mapping(target = "viewedAt",expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    @Mapping(target = "firstViewedAt",expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    LinkStatisticEntity toEntity(CreateLinkStatistic request);




}
