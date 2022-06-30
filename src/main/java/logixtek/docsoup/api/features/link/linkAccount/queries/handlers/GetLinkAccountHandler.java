package logixtek.docsoup.api.features.link.linkAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.link.linkAccount.queries.GetLinkAccount;
import logixtek.docsoup.api.infrastructure.models.LinkAccountWithActivityInfor;
import logixtek.docsoup.api.infrastructure.repositories.LinkAccountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("GetLinkAccountHandler")
@AllArgsConstructor
public class GetLinkAccountHandler implements Command.Handler<GetLinkAccount, ResponseEntity<LinkAccountWithActivityInfor>> {
    private final LinkAccountsRepository linkAccountsRepository;

    @Override
    public ResponseEntity<LinkAccountWithActivityInfor> handle(GetLinkAccount query) {
        var accountOption =
                linkAccountsRepository.getLinkAccountsById(query.getId());

        if (!accountOption.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accountOption.get());
    }
}
