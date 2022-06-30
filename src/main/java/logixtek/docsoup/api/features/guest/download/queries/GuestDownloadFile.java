package logixtek.docsoup.api.features.guest.download.queries;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor(staticName = "of")
public class GuestDownloadFile implements Command<ResponseEntity<Resource>> {
    String fileType;

    @Length(min = 1)
    String resourceId;
}
