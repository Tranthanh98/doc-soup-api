package logixtek.docsoup.api.features.payment.models;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class LastFailedPayment {
    PaypalAmount amount;
    OffsetDateTime time;
}
