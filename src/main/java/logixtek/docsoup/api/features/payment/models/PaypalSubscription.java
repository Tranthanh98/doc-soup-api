package logixtek.docsoup.api.features.payment.models;


import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaypalSubscription {
    String id;
    String status;
    String plan_id;
    String status_update_time;
    String quantity;
    OffsetDateTime create_time;
    String custom_id;
    OffsetDateTime start_time;
    OffsetDateTime update_time;
    PaypalAmount amount;
    BillingInfo billing_info;
}
