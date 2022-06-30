package logixtek.docsoup.api.features.dataroom.queries;

import logixtek.docsoup.api.infrastructure.commands.PaginationCommand;
import logixtek.docsoup.api.infrastructure.models.SimplifiedDataRoomInfo;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class SearchDataRoom extends PaginationCommand<ResponseEntity<PageResultOf<SimplifiedDataRoomInfo>>> {
    @Length(min = 3)
    private String keyword;
}
