package logixtek.docsoup.api.features.contact.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.queries.GetContact;
import logixtek.docsoup.api.features.contact.responses.ContactDetail;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("GetContactHandler")
@AllArgsConstructor
public class GetContactHandler  implements Command.Handler<GetContact, ResponseEntity<ContactDetail>>  {
    private final ContactRepository contactEntityRepository;
    private final PermissionService permissionService;
    @Override
    public ResponseEntity<ContactDetail> handle(GetContact command) {
        var contactOption = contactEntityRepository.findById(command.getId());

        if(!contactOption.isPresent()) {
            return  ResponseEntity.notFound().build();
        }

        var contact = contactOption.get();

        if(permissionService.getOfContact(contact,command).canRead())
        {
            var contactDetailWithLinkModels = contactEntityRepository.findAllLinkNameAndSignedNDAByContactId(contact.getId());
            List<String> listContact = new ArrayList<>();
            var result = new ContactDetail(contact, listContact,false);

            if (contactDetailWithLinkModels.isPresent()){
                contactDetailWithLinkModels.get().forEach(link -> {
                    result.getLinkNames().add(link.getLinkName());

                    if(Boolean.TRUE.equals(link.getSignedNDA())){
                        result.setSignedNDA(true);
                    }
                });
            }
            return ResponseEntity.ok(result);
        }
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
