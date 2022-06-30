package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.SearchLinkAccount;
import logixtek.docsoup.api.infrastructure.models.SimplifiedLinkAccountInfo;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("SearchLinkAccountHandler")
public class SearchLinkAccountHandler implements Command.Handler<SearchLinkAccount, ResponseEntity<PageResultOf<SimplifiedLinkAccountInfo>>> {

    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<PageResultOf<SimplifiedLinkAccountInfo>> handle(SearchLinkAccount query) {

        Pageable pageable = PageRequest.of(query.getPage(),
                query.getPageSize(),
                Sort.by(Sort.Direction.DESC, "name"));

        var result = linkAccountsRepository.findAllByCompanyIdAndNameContains(
                query.getCompanyId(),
                query.getKeyword(),
                pageable);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(PageResultOf.of(result.getContent(),
                query.getPage(),
                result.getTotalElements(),
                result.getTotalPages()));

    }
}
