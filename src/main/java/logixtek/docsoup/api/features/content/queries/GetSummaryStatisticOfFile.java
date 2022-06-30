package logixtek.docsoup.api.features.content.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.SummaryStatisticOnFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class GetSummaryStatisticOfFile extends BaseIdentityCommand<ResponseEntity<List<SummaryStatisticOnFile>>> {

    Long fileId;
}
