package logixtek.docsoup.api.features.contact.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.ContactSearchInfo;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@Data
public class SearchContact extends PaginationCommand<ResponseEntity<PageResultOf<ContactSearchInfo>>> {
    @Length(min = 3)
    String keyword;

}
