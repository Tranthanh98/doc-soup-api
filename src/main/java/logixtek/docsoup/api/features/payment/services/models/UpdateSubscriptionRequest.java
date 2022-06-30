package logixtek.docsoup.api.features.payment.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class UpdateSubscriptionRequest {
    String plan_id;

    String quantity;

    String return_url;

    String cancel_url;
}
