package logixtek.docsoup.api.features.payment.billinginfo.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class PayPalSubscription {
    String id;

    String paypalSubscriptionStatus;
}
