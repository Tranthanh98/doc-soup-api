package logixtek.docsoup.api.features.contact.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.contact.commands.CreateContact;
import logixtek.docsoup.api.features.share.domainevents.CreateContactDomainEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CreateContactDomainEventHandler")
@AllArgsConstructor
public class CreateContactDomainEventHandler implements Notification.Handler<CreateContactDomainEvent> {

    private final Pipeline pipeline;

    @Override
    public void handle(CreateContactDomainEvent domainEvent) {
        var createContactCommand = new CreateContact();
        createContactCommand.setEmail(domainEvent.getEmail());
        createContactCommand.setName(domainEvent.getName());
        createContactCommand.setAccountId(domainEvent.getAccountId());
        createContactCommand.setCompanyId(domainEvent.getCompanyId());

        createContactCommand.execute(pipeline);
    }
}
