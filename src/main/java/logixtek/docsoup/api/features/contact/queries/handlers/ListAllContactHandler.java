package logixtek.docsoup.api.features.contact.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.queries.ListAllContact;
import logixtek.docsoup.api.infrastructure.models.ContactWithLinkModel;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component("ListAllContactHandler")
@AllArgsConstructor
public class ListAllContactHandler implements Command.Handler<ListAllContact, ResponseEntity<Collection<ContactWithLinkModel>>> {

    private final ContactRepository contactEntityRepository;

    @Override
    public ResponseEntity<Collection<ContactWithLinkModel>> handle(ListAllContact query) {
        var contact = contactEntityRepository
                .findAllContactWithLinkModelByCompanyIdAndAccountIdAndModeAndArchived(query.getCompanyId().toString(), query.getAccountId(), query.getMode(), query.getIncludeArchived() ? null : false);

        var result = new ArrayList<>(contact);
        result.sort((x, y) -> y.getLastActivity().compareTo(x.getLastActivity()));

        return ResponseEntity.ok(result);
    }
}
