package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CreateContactDomainEvent implements Notification {
    String email;

    String name;

    String accountId;

    UUID companyId;
}
