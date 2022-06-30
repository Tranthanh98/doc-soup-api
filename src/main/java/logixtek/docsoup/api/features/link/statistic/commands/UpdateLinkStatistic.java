package logixtek.docsoup.api.features.link.statistic.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.statistic.dto.StatisticData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.UUID;


@Getter
@Setter
@Builder
public class UpdateLinkStatistic implements Command<ResponseEntity<String>> {
    UUID linkId;
    String deviceId;
    String sessionId;
    Long viewerId;
    Collection<StatisticData> data;

}


