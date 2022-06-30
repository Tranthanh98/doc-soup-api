package logixtek.docsoup.api.features.administrator.dashboard.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseAdminIdentityCommand;
import logixtek.docsoup.api.infrastructure.enums.PeriodicalFilter;
import logixtek.docsoup.api.infrastructure.models.LinkAndDocuments;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor(staticName = "of")
public class GetLinksAndDocuments extends BaseAdminIdentityCommand<ResponseEntity<Collection<LinkAndDocuments>>> {
    PeriodicalFilter groupBy;

    LocalDate startDate;

    LocalDate endDate;
}
