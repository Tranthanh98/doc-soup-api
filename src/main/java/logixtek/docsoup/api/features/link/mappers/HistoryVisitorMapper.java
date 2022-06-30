package logixtek.docsoup.api.features.link.mappers;

import logixtek.docsoup.api.features.link.view.commands.CreateVisitorHistory;
import logixtek.docsoup.api.infrastructure.entities.HistoryVisitorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HistoryVisitorMapper {
    HistoryVisitorMapper INSTANCE = Mappers.getMapper(HistoryVisitorMapper.class);

    @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))")
    HistoryVisitorEntity toEntity(CreateVisitorHistory source);
}
