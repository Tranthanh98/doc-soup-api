package logixtek.docsoup.api.features.share.document.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.DeleteDocumentDomainEvent;
import logixtek.docsoup.api.infrastructure.thirdparty.Impl.StreamDocsDocumentService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component("DeleteDocumentDomainEventHandler")
@AllArgsConstructor
public class DeleteDocumentDomainEventHandler implements Notification.Handler<DeleteDocumentDomainEvent> {
    private final StreamDocsDocumentService documentService;

    @Override
    @SneakyThrows
    public void handle(DeleteDocumentDomainEvent notification) {
        if(!Strings.isNullOrEmpty(notification.getSecureId())) {
            documentService.Delete(notification.getSecureId());
        }
    }
}
