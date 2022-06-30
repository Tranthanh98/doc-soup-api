package logixtek.docsoup.api.features.contact.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.queries.SearchContact;
import logixtek.docsoup.api.infrastructure.models.ContactSearchInfo;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SearchContactHandler")
@AllArgsConstructor
public class SearchContactHandler implements Command.Handler<SearchContact, ResponseEntity<PageResultOf<ContactSearchInfo>>> {

    private final ContactRepository contactRepository;

    @Override
    public ResponseEntity<PageResultOf<ContactSearchInfo>> handle(SearchContact query) {
        Pageable pageable = PageRequest.of(query.getPage(),
                query.getPageSize(),
                Sort.Direction.DESC,
                "name");

        var result = contactRepository.searchContact(query.getKeyword(), query.getCompanyId().toString(), pageable);

        return ResponseEntity.ok(
                PageResultOf.of(result.getContent(),
                        query.getPage(),
                        result.getTotalElements(),
                        result.getTotalPages()));
    }
}
