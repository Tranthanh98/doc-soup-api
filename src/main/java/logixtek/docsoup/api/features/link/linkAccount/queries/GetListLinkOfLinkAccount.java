package logixtek.docsoup.api.features.link.linkAccount.queries;


import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.LinkWithStatistic;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor(staticName = "of")
public class GetListLinkOfLinkAccount extends PaginationCommand<ResponseEntity<PageResultOf<LinkWithStatistic>>> {
    Long linkAccountId;

    String filterBy;
}
