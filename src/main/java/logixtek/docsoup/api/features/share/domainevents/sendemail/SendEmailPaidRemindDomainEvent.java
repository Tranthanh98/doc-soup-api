package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailPaidRemindDomainEvent implements Notification {
    @Getter
    @Setter
    Long paymentHistoryId;
}
