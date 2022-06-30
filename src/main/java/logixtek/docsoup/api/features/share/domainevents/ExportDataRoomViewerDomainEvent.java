package logixtek.docsoup.api.features.share.domainevents;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ExportDataRoomViewerDomainEvent implements Notification {
    String accountId;
    Long dataRoomId;
}
