package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailInvitationRequestMessage;
import lombok.Getter;
import lombok.Setter;

public class SendEmailInvitationDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailInvitationRequestMessage sendEmailInvitationRequest;
}
