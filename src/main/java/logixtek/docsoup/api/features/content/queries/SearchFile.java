package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.ContentResult;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class SearchFile extends PaginationCommand<ResponseEntity<PageResultOf<ContentResult>>> {
    @Length(min = 3)
    String keyword;
}
