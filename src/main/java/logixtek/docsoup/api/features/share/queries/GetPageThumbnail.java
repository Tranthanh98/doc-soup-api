package logixtek.docsoup.api.features.share.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetPageThumbnail extends BaseIdentityCommand<ResponseEntity<Resource>> {
    UUID documentId;
    Integer pageNumber;
}
