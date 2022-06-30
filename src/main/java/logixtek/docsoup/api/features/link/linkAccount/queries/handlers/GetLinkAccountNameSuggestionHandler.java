package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetLinkAccountNameSuggestion;
import logixtek.docsoup.api.infrastructure.entities.LinkAccountsEntity;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("GetLinkAccountNameSuggestionHandler")
@AllArgsConstructor
public class GetLinkAccountNameSuggestionHandler implements Command.Handler<GetLinkAccountNameSuggestion, ResponseEntity<Collection<LinkAccountsEntity>>> {

    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<Collection<LinkAccountsEntity>> handle(GetLinkAccountNameSuggestion query) {
        if(query.getKeyword() != null && !query.getKeyword().isBlank()){
            var linkAccountOption = linkAccountsRepository
                    .findTop10ByCompanyIdAndArchivedIsFalseAndNameContainingOrderByName(query.companyId, query.getKeyword());

            return ResponseEntity.ok(linkAccountOption);
        }

        return ResponseEntity.ok(Collections.emptyList());
    }
}
