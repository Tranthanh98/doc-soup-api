package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetListLinkAccount;
import logixtek.docsoup.api.features.link.linkAccount.responses.LinkAccountViewModel;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetListLinkAccountHandler")
@AllArgsConstructor
public class GetListLinkAccountHandler implements Command.Handler<GetListLinkAccount, ResponseEntity<LinkAccountViewModel>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<LinkAccountViewModel> handle(GetListLinkAccount query) {
        var items = linkAccountsRepository
                .findAllByStatusAndModeAndArchivedAndCompanyIdAndAccountId(query.getStatus(), query.getMode(), query.getArchived(), query.getCompanyId().toString(), query.getAccountId());

        var totalLinkAccount = linkAccountsRepository.countAllByCompanyId(query.getCompanyId());

        var result = LinkAccountViewModel.of(totalLinkAccount, items);

        return ResponseEntity.ok(result);
    }
}
