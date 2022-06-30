package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailDisableLinkRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailDisableLinkDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailDisableLinkRequestMessage sendEmailDisableLinkRequestMessage;
}
