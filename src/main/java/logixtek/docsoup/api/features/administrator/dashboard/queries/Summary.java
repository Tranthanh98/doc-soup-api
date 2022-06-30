package logixtek.docsoup.api.features.administrator.dashboard.queries;

import logixtek.docsoup.api.infrastructure.models.SummaryItemViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public class Summary extends BaseAdminIdentityCommand<ResponseEntity<Collection<SummaryItemViewModel>>> {

}
