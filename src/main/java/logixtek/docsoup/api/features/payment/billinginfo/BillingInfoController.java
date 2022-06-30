package logixtek.docsoup.api.features.payment.billinginfo;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.payment.billinginfo.commands.CreateOrEditBillingContact;
import logixtek.docsoup.api.features.payment.billinginfo.commands.CreateOrEditBillingInfo;
import logixtek.docsoup.api.features.payment.billinginfo.commands.SendEmailInvoice;
import logixtek.docsoup.api.features.payment.billinginfo.queries.GetCurrentActivePaypalSubscription;
import logixtek.docsoup.api.features.payment.billinginfo.queries.ListAllPaymentHistory;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("billing")
public class BillingInfoController extends BaseController {
    public BillingInfoController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping()
    public ResponseEntity<?> listPaymentHistory(){
        var query = new ListAllPaymentHistory();

        return handleWithResponseMessage(query);
    }

    @GetMapping("/current-paypal-subscription")
    public ResponseEntity<?> getCurrentActiveSubscription(){
        var query = new GetCurrentActivePaypalSubscription();

        return handleWithResponseMessage(query);
    }

    @PostMapping("/billing-contact")
    public ResponseEntity<?> createOrEditBillingContact(@Valid @RequestBody CreateOrEditBillingContact command, BindingResult bindingResult){
        return handleWithResponse(command, bindingResult);
    }

    @PostMapping("/invoice-billing-info")
    public ResponseEntity<?> createOrEditBillingInfo(@Valid @RequestBody CreateOrEditBillingInfo command, BindingResult bindingResult){
        return handleWithResponse(command, bindingResult);
    }

    @PostMapping("/{id}/send-invoice")
    public ResponseEntity<?> sendEmailInvoice(@PathVariable Long id){
        var command = new SendEmailInvoice();
        command.setPaymentHistoryId(id);
        return handleWithResponse(command);
    }
}
