package logixtek.docsoup.api.features.content.mappers;

import logixtek.docsoup.api.features.content.responses.AggregateContentViewModel;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileEntityMapper {
    FileEntityMapper INSTANCE = Mappers.getMapper(FileEntityMapper.class);

    @Mapping(target = "isFile", constant = "true")
    @Mapping(target = "visits", source="recentVisits", defaultValue = "0")
    @Mapping(target = "name", source = "displayName")
    AggregateContentViewModel toViewModel(FileEntityWithVisits request);
}
