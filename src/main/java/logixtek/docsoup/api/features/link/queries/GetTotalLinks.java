package logixtek.docsoup.api.features.link.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class GetTotalLinks extends BaseIdentityCommand<ResponseEntity<Integer>> {
}
