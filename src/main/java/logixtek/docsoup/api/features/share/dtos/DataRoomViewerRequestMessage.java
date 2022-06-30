package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@Builder
@NoArgsConstructor
public class DataRoomViewerRequestMessage {
    String accountId;
    Long dataRoomId;
}
