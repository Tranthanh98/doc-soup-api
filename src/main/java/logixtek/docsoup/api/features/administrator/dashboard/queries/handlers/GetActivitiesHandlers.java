package logixtek.docsoup.api.features.administrator.dashboard.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.dashboard.queries.GetActivities;
import logixtek.docsoup.api.infrastructure.models.ActivityWithLinkAndVisit;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("GetActivitiesHandlers")
@AllArgsConstructor
public class GetActivitiesHandlers implements Command.Handler<GetActivities, ResponseEntity<Collection<ActivityWithLinkAndVisit>>> {

    private final DocumentRepository repository;

    @Override
    public ResponseEntity<Collection<ActivityWithLinkAndVisit>> handle(GetActivities query) {
        if(query.getEndDate() == null && query.getStartDate() == null){
            return ResponseEntity.badRequest().build();
        }

        var result = repository.getActivities(query.getGroupBy().name(), query.getStartDate(), query.getEndDate());

        return ResponseEntity.ok(result);
    }
}
