package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.ReregisterDocumentRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ReregisterDocumentDomainEvent implements Notification {
    ReregisterDocumentRequestMessage reregisterDocumentRequestMessage;
}
