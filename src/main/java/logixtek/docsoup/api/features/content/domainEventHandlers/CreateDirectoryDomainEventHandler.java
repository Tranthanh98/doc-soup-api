package logixtek.docsoup.api.features.content.domainEventHandlers;


import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.commands.CreateDirectory;
import logixtek.docsoup.api.features.share.domainevents.CreateDirectoryDomainEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CreateDirectoryDomainEventHandler")
@AllArgsConstructor
public class CreateDirectoryDomainEventHandler implements Notification.Handler<CreateDirectoryDomainEvent> {

    private final Pipeline pipeline;
    
    @Override
    public void handle(CreateDirectoryDomainEvent domainEvent) {
        var createDirectoryCommand = new CreateDirectory();

        createDirectoryCommand.setName(domainEvent.getName());
        createDirectoryCommand.setIsTeam(domainEvent.getIsTeam());
        createDirectoryCommand.setParentId(domainEvent.getParentId());
        createDirectoryCommand.setCompanyId(domainEvent.getCompanyId());
        createDirectoryCommand.setAccountId(domainEvent.getAccountId());

        createDirectoryCommand.execute(pipeline);
    }
    
}
