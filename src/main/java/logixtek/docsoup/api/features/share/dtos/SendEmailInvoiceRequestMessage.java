package logixtek.docsoup.api.features.share.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SendEmailInvoiceRequestMessage {
    @Getter
    @Setter
    String accountId;

    @Getter
    @Setter
    Long paymentHistoryId;
}
