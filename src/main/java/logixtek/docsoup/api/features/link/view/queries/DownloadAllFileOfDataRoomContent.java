package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@Builder
public class DownloadAllFileOfDataRoomContent implements Command<ResponseEntity<Resource>> {

    UUID linkId;

    String deviceId;

    Long viewerId;

}
