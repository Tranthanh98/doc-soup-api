package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SendEmailInvitationRequestMessage {
    @Getter
    @Setter
    String senderAccountId;

    @Getter
    @Setter
    Long companyUserId;

    @Getter
    @Setter
    Integer numberOfSend;
}
