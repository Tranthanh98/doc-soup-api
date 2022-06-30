package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailRemindInvitationRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailRemindInvitationDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailRemindInvitationRequestMessage requestMessage;
}
