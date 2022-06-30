package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SendEmailPaymentPaidEndedRequestMessage {
    @Getter
    @Setter
    Long paymentHistoryId;

    @Getter
    @Setter
    Long previousPlanTierId;
}
