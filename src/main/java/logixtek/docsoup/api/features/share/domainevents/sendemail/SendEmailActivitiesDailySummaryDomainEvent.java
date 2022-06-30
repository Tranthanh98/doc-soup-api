package logixtek.docsoup.api.features.share.domainevents.sendemail;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.share.dtos.SendEmailActivitiesDailySummaryRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
public class SendEmailActivitiesDailySummaryDomainEvent implements Notification {
    @Getter
    @Setter
    SendEmailActivitiesDailySummaryRequestMessage requestMessage;
}
