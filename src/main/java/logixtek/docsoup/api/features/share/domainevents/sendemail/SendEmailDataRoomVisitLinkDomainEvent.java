package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailDataRoomVisitLinkRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailDataRoomVisitLinkDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailDataRoomVisitLinkRequestMessage sendEmailDataRoomVisitLinkRequestMessage;
}
