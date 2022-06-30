package logixtek.docsoup.api.features.administrator.publicAccount.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.PublicAccount;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class ListPublicAccount extends PaginationCommand<ResponseEntity<PageResultOf<PublicAccount>>> {

    String keyword;

}
