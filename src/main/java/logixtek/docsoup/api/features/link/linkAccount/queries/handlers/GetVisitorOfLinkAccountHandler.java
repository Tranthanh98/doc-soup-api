package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetVisitorOfLinkAccount;
import logixtek.docsoup.api.infrastructure.models.LinkAccountVisitor;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetVisitorOfLinkAccountHandler")
@AllArgsConstructor
public class GetVisitorOfLinkAccountHandler implements Command.Handler<GetVisitorOfLinkAccount, ResponseEntity<PageResultOf<LinkAccountVisitor>>> {

    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<PageResultOf<LinkAccountVisitor>> handle(GetVisitorOfLinkAccount query) {
        var linkAccountOption = linkAccountsRepository.findById(query.getLinkAccountId());

        if(!linkAccountOption.isPresent()){
            return ResponseEntity.notFound().build();
        }

        var linkAccount = linkAccountOption.get();

        if(!linkAccount.getCompanyId().equals(query.getCompanyId())){
            return ResponseEntity.notFound().build();
        }


        Pageable pageable = PageRequest
                .of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "viewedAt"));

        var linkAccountVisitors = linkAccountsRepository
                .getVisitorOfLinkAccount(query.getLinkAccountId(), query.getCompanyId(), pageable);

        var result = PageResultOf
                .of(linkAccountVisitors.getContent(),
                    query.getPage(),
                    linkAccountVisitors.getTotalElements(),
                    linkAccountVisitors.getTotalPages()
                );
        return ResponseEntity.ok(result);
    }
}
