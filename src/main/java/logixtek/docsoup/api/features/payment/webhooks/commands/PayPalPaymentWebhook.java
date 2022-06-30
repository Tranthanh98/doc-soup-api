package logixtek.docsoup.api.features.payment.webhooks.commands;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.models.PaypalPaymentWebHook;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

@Data
public class PayPalPaymentWebhook implements Command<ResponseEntity<String>> {
    String id;
    OffsetDateTime created_time;
    String resource_type;
    String event_type;
    String summary;
    PaypalPaymentWebHook resource;
    String status;
    String token;
}
