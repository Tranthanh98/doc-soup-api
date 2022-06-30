package logixtek.docsoup.api.features.link.domainEventHandlers;

import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.share.document.commands.DeleteDocument;
import logixtek.docsoup.api.features.share.domainevents.DeleteLinkDomainEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("DeleteLinkDomainEventHandler")
@AllArgsConstructor
public class DeleteLinkDomainEventHandler implements Notification.Handler<DeleteLinkDomainEvent> {
    private final Pipeline pipeline;
    @Override
    public void handle(DeleteLinkDomainEvent event) {
        if(event.getDocumentId() != null) {
            var deleteDocumentCommand = new DeleteDocument(event.getDocumentId());
            deleteDocumentCommand.execute(pipeline);
        }
    }
}
