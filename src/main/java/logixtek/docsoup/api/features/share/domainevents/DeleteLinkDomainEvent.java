package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class DeleteLinkDomainEvent implements Notification {

    UUID documentId;
}
