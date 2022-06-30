package logixtek.docsoup.api.features.contact.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.contact.commands.CreateContact;
import logixtek.docsoup.api.features.contact.mappers.ContactMapper;
import logixtek.docsoup.api.infrastructure.entities.ContactEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CreateContactHandler")
@AllArgsConstructor
public class CreateContactHandler implements Command.Handler<CreateContact, ResultOf<Long>> {

    private final ContactRepository contactEntityRepository;

    @Override
    public ResultOf<Long> handle(CreateContact createContact) {

        var contactOption = contactEntityRepository
                .findFirstByEmailAndCompanyId(createContact.getEmail(), createContact.getCompanyId());

        ContactEntity contactEntity;
        if(contactOption.isPresent()){
            contactEntity = ContactMapper.INSTANCE.toEntity(contactOption.get(), createContact);
        }
        else{
            contactEntity = ContactMapper.INSTANCE.toEntity(createContact);
        }

        var contact = contactEntityRepository.saveAndFlush(contactEntity);

        return ResultOf.of(contact.getId());
    }
}
