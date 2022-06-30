package logixtek.docsoup.api.features.administrator.publicAccount.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.publicAccount.queries.ListPublicAccount;
import logixtek.docsoup.api.infrastructure.models.PublicAccount;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("ListPublicAccountHandler")
@AllArgsConstructor
public class ListPublicAccountHandler implements Command.Handler<ListPublicAccount, ResponseEntity<PageResultOf<PublicAccount>>> {

    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<PageResultOf<PublicAccount>> handle(ListPublicAccount query) {

        var accountUsers = accountRepository.searchAccountByNameOrEmail(query.getKeyword(),
                query.getPage(),
                query.getPageSize());


        if (accountUsers.isEmpty() || accountUsers.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var accountsResult = accountUsers.get();

        var totalRows = accountsResult.stream().findFirst().get().getTotalRows();

        var totalPages = (totalRows % query.getPageSize() == 0) ?
                totalRows / query.getPageSize()
                : totalRows / query.getPageSize() + 1;

        var result = PageResultOf.of(accountsResult,
                query.getPage(),
                totalRows,
                totalPages);

        return ResponseEntity.ok(result);
    }
}
