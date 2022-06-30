package logixtek.docsoup.api.features.report.queires;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.models.DocumentActivity;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class GetDocumentActivity extends BaseIdentityCommand<ResponseMessageOf<List<DocumentActivity>>> {
    Integer top;
    int dateRecent = 30;
    String sortDirection = Sort.Direction.DESC.name();
}
