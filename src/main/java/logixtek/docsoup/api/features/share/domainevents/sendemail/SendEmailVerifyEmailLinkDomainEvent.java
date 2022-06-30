package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailVerifyEmailLinkRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class SendEmailVerifyEmailLinkDomainEvent implements Notification {
    SendEmailVerifyEmailLinkRequestMessage requestMessage;
}