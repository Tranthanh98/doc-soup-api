package logixtek.docsoup.api.features.link.statistic.mappers;

import logixtek.docsoup.api.features.link.statistic.commands.CreateLinkStatistic;
import logixtek.docsoup.api.features.link.view.queries.GetLink;
import logixtek.docsoup.api.features.link.view.queries.GetLinkFromEmail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateLinkStatisticMapper {

    CreateLinkStatisticMapper INSTANCE = Mappers.getMapper( CreateLinkStatisticMapper.class );

    @Mapping(target = "documentId",ignore = true)
    @Mapping(target = "authorizedAt",ignore = true)
    @Mapping(target = "deviceAgent",source = "userAgent")
    CreateLinkStatistic toCommand(GetLink request);

    @Mapping(target = "documentId",ignore = true)
    @Mapping(target = "authorizedAt",ignore = true)
    @Mapping(target = "deviceAgent",source = "userAgent")
    CreateLinkStatistic toCommand(GetLinkFromEmail request);

}
