package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.ViewerLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ListViewerLocationOfFile extends BaseIdentityCommand<ResponseEntity<Collection<ViewerLocation>>> {
    long fileId;
}
