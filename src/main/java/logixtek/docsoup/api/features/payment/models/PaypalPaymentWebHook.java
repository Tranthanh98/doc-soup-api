package logixtek.docsoup.api.features.payment.models;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaypalPaymentWebHook {
    String id;
    String billing_agreement_id;
    String state;
    OffsetDateTime create_time;
    OffsetDateTime update_time;
    OffsetDateTime start_time;
    PaypalAmount amount;
}
