package logixtek.docsoup.api.features.share.domainevents;
import an.awesome.pipelinr.Notification;
import lombok.Getter;
import lombok.Setter;

public class DeleteDocumentDomainEvent implements Notification {
    @Getter
    @Setter
    String secureId;
}
