package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter
@Setter
public class SendEmailDataRoomVisitLinkRequestMessage {
    String userName;
    String bucketKey;
    String emailTo;
    String dataRoomName;
}
