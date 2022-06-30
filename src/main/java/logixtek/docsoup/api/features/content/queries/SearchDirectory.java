package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.ContentResult;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class SearchDirectory extends PaginationCommand<ResponseEntity<PageResultOf<ContentResult>>> {
    @Length(min = 3)
    String keyword;
}
