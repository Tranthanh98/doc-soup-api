package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailAcceptedInvitationRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailAcceptedInvitationDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailAcceptedInvitationRequestMessage requestMessage;
}
