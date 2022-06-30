package logixtek.docsoup.api.features.contact.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.queries.ListFileOfContact;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.models.FileStatisticOnContact;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListFileOfContactHandler")
@AllArgsConstructor
@Getter
@Setter
public class ListFileOfContactHandler implements Command.Handler<ListFileOfContact, ResponseEntity<Collection<FileStatisticOnContact>>> {

    private final ContactRepository contactRepository;
    private final FileRepository fileRepository;
    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<Collection<FileStatisticOnContact>> handle(ListFileOfContact query) {
        var contactOption = contactRepository.findById(query.getContactId());

        if(!contactOption.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        var contact = contactOption.get();

        if(permissionService.getOfContact(contact,query).canRead()) {

            var resultOption = fileRepository.findAllFileByContactId(query.getContactId());

            if (!resultOption.isPresent()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(resultOption.get());
        }

        return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
