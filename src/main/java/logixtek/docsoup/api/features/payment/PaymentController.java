package logixtek.docsoup.api.features.payment;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.payment.commands.UpgradeDowngrade;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("payment")
public class PaymentController extends BaseController {
    public PaymentController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @PostMapping("/upgrade-downgrade")
    public ResponseEntity<?>  upgradeOrDowngradePlan(@Valid @RequestBody UpgradeDowngrade command, BindingResult bindingResult) {
        return handleWithResponse(command, bindingResult);
    }

}
