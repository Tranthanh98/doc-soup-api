package logixtek.docsoup.api.features.payment.billinginfo.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

public class SendEmailInvoice extends BaseIdentityCommand<ResponseEntity<String>> {
    @Getter
    @Setter
    Long paymentHistoryId;
}
