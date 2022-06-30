package logixtek.docsoup.api.features.link.linkAccount.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.SimplifiedLinkAccountInfo;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Setter
@Getter
public class SearchLinkAccount extends PaginationCommand<ResponseEntity<PageResultOf<SimplifiedLinkAccountInfo>>> {

    @Length(min = 3)
    String keyword;
}
