package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateCertificateAndSignedDomainEvent implements Notification {
    UUID linkId;
    Long viewerId;
    Long contactId;
}
