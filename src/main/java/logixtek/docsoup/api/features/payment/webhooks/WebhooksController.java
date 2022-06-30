package logixtek.docsoup.api.features.payment.webhooks;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.payment.webhooks.commands.PayPalPaymentWebhook;
import logixtek.docsoup.api.features.payment.webhooks.commands.PaypalSubscriptionWebhook;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("webhooks")
@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
public class WebhooksController extends BaseController {
    public WebhooksController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @PostMapping("/paypal-webhooks/subscriptions/{token}")
    public ResponseEntity<?>  listenPaypalSubscriptionWebhook(@PathVariable String token, @Valid @RequestBody PaypalSubscriptionWebhook command, BindingResult bindingResult) {
        command.setToken(token);
        return handleWithResponse(command, bindingResult);
    }

    @PostMapping("/paypal-webhooks/payments/{token}")
    public ResponseEntity<?>  listenPaypalPaymentWebhook(@PathVariable String token, @Valid @RequestBody PayPalPaymentWebhook command, BindingResult bindingResult) {
        command.setToken(token);
        return handleWithResponse(command, bindingResult);
    }
}
