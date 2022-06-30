package logixtek.docsoup.api.features.contact.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.commands.UpdateContactStatus;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpdateContactStatusHandler")
@AllArgsConstructor
public class UpdateContactStatusHandler implements Command.Handler<UpdateContactStatus, ResponseEntity<String>> {

    private final ContactRepository contactEntityRepository;

    private  final PermissionService permissionService;
    @Override
    public ResponseEntity<String> handle(UpdateContactStatus command) {

        var contactOption = contactEntityRepository.findById(command.getContactId());
        if(!contactOption.isPresent())
        {
            return  ResponseEntity.notFound().build();
        }

        var contact = contactOption.get();

        if(!permissionService.getOfContact(contact,command).canWrite())
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        contact.setArchived(command.getArchived());

        contactEntityRepository.saveAndFlush(contact);
        
        return ResponseEntity.accepted().build();

    }
}
