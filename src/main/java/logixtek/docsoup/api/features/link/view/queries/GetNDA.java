package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Data
@FieldNameConstants
@AllArgsConstructor(staticName = "of")
public class GetNDA implements Command<ResponseEntity<Resource>> {
    UUID linkId;
    String deviceId;
    Long viewerId ;
    String token;
    String ip;
}
