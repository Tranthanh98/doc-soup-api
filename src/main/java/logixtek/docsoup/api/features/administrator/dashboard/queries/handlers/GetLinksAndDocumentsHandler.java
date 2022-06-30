package logixtek.docsoup.api.features.administrator.dashboard.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.dashboard.queries.GetLinksAndDocuments;
import logixtek.docsoup.api.infrastructure.models.LinkAndDocuments;
import logixtek.docsoup.api.infrastructure.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("GetLinksAndDocumentsHandler")
@AllArgsConstructor
public class GetLinksAndDocumentsHandler implements Command.Handler<GetLinksAndDocuments, ResponseEntity<Collection<LinkAndDocuments>>> {

    private final DocumentRepository repository;

    @Override
    public ResponseEntity<Collection<LinkAndDocuments>> handle(GetLinksAndDocuments query) {
        if(query.getEndDate() == null && query.getStartDate() == null){
            return ResponseEntity.badRequest().build();
        }

        var result = repository.getDocumentsAndLinks(query.getGroupBy().name(), query.getStartDate(), query.getEndDate());

        return ResponseEntity.ok(result);
    }
}
