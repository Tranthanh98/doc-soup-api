package logixtek.docsoup.api.features.administrator.dashboard.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.dashboard.queries.Summary;
import logixtek.docsoup.api.infrastructure.models.SummaryItemViewModel;
import logixtek.docsoup.api.infrastructure.repositories.InternalAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("SummaryHandler")
@AllArgsConstructor
public class SummaryHandler implements Command.Handler<Summary, ResponseEntity<Collection<SummaryItemViewModel>>> {

    private final InternalAccountRepository repository;

    @Override
    public ResponseEntity<Collection<SummaryItemViewModel>> handle(Summary query) {

        var result = repository.getSummary();

        return ResponseEntity.ok(result);
    }
}
