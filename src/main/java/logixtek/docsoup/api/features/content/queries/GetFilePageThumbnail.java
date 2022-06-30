package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;


@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetFilePageThumbnail extends BaseIdentityCommand<ResponseEntity<Resource>>  {
    Long fileId;
    Integer page;

    Integer version;
}
