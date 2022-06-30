package logixtek.docsoup.api.features.link.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.SimplifiedLinkInformation;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Data
public class SearchLink extends PaginationCommand<ResponseEntity<PageResultOf<SimplifiedLinkInformation>>> {

    @Length(min = 3)
    String keyword;
}
