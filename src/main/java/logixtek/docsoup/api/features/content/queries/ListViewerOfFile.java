package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
public class ListViewerOfFile extends PaginationCommand<ResponseEntity<PageResultOf<Viewer>>> {
    Long fileId;
}
