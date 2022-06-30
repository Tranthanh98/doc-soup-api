package logixtek.docsoup.api.features.payment.webhooks.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.models.PaypalSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor(staticName = "of")
public class PaypalSubscriptionWebhook implements Command<ResponseEntity<String>> {
    String id;
    OffsetDateTime created_time;
    String resource_type;
    String event_type;
    String summary;
    PaypalSubscription resource;
    String status;
    String token;
}
