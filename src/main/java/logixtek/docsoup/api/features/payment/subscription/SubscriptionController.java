package logixtek.docsoup.api.features.payment.subscription;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.payment.subscription.queries.GetCurrentSubscription;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment/subscription")
public class SubscriptionController extends BaseController {
    public SubscriptionController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription(){
        var query =  new GetCurrentSubscription();
        return handleWithResponseMessage(query);
    }
}
