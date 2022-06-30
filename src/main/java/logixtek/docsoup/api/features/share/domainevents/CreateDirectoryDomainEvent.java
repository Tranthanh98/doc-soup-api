package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
public class CreateDirectoryDomainEvent implements Notification {
 
    private long parentId = 0;

    private String name;

    Boolean isTeam;

    String accountId;

    UUID companyId;
}
