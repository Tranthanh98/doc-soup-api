package logixtek.docsoup.api.features.link.view.queries;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Min;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@Builder
public class GetPageThumbnailOfDataroomContent implements Command<ResponseEntity<Resource>> {
    UUID linkId;

    @Min(1)
    Long fileId;

    @Min(1)
    Integer pageNumber;

    String deviceId;

    Long viewerId;
}
