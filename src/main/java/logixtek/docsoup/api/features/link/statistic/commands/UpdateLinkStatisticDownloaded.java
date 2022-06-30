package logixtek.docsoup.api.features.link.statistic.commands;

import an.awesome.pipelinr.Command;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UpdateLinkStatisticDownloaded implements Command<ResponseEntity<String>> {
    UUID linkId;
    String deviceId;
    String sessionId;
    Long viewerId;
}
