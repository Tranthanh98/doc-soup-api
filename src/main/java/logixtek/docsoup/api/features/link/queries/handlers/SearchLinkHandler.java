package logixtek.docsoup.api.features.link.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.queries.SearchLink;
import logixtek.docsoup.api.infrastructure.models.SimplifiedLinkInformation;
import logixtek.docsoup.api.infrastructure.repositories.LinkRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("SearchLinkHandler")
public class SearchLinkHandler implements Command.Handler<SearchLink, ResponseEntity<PageResultOf<SimplifiedLinkInformation>>> {

    private final LinkRepository linkRepository;

    @Override
    public ResponseEntity<PageResultOf<SimplifiedLinkInformation>> handle(SearchLink query) {

            Pageable pageable = PageRequest.of(query.getPage(),
                    query.getPageSize(),
                    Sort.Direction.DESC,
                    "name");

            var linksResult = linkRepository.searchLinkWithKeyword(query.getKeyword(),
                    query.getCompanyId(),
                    query.getAccountId(),
                    pageable);

            if(linksResult.isEmpty()){
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(
                    PageResultOf.of(linksResult.getContent(),
                    query.getPage(),
                    linksResult.getTotalElements(),
                    linksResult.getTotalPages()));

    }
}
