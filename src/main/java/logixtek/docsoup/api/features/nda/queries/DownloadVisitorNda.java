package logixtek.docsoup.api.features.nda.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor(staticName = "of")
public class DownloadVisitorNda extends BaseIdentityCommand<ResponseEntity<Resource>> {
    Long viewerId;
}
