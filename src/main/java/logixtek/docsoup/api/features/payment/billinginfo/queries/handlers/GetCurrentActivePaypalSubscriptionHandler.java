package logixtek.docsoup.api.features.payment.billinginfo.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.billinginfo.queries.GetCurrentActivePaypalSubscription;
import logixtek.docsoup.api.features.payment.billinginfo.responses.PayPalSubscription;
import logixtek.docsoup.api.features.payment.services.PaymentGatewayService;
import logixtek.docsoup.api.features.payment.services.SubscriptionService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("GetCurrentActiveSubscriptionStatusHandler")
@AllArgsConstructor
public class GetCurrentActivePaypalSubscriptionHandler implements Command.Handler<GetCurrentActivePaypalSubscription, ResponseMessageOf<PayPalSubscription>> {
    private final PaymentGatewayService paymentGatewayService;
    private final CompanyUserCacheService companyUserCacheService;
    private final SubscriptionService subscriptionService;

    @Override
    public ResponseMessageOf<PayPalSubscription> handle(GetCurrentActivePaypalSubscription query) {

        var companyUser = companyUserCacheService.get(query.getAccountId(), query.getCompanyId());

        if(companyUser == null){
            return new ResponseMessageOf<>(HttpStatus.UNAUTHORIZED, ResponseResource.NotFoundCompany, Map.of());
        }

        if(companyUser.getMember_type() != CompanyUserConstant.OWNER_TYPE){
            return new ResponseMessageOf<>(HttpStatus.UNAUTHORIZED, ResponseResource.DonNotHavePermission, Map.of());
        }

        var currentSubscriptionOption = subscriptionService.get(companyUser.getCompanyId());

        if(currentSubscriptionOption.isPresent()) {
            var currentSubscription = currentSubscriptionOption.get();
            var subscriptionPayload = paymentGatewayService.getSubscriptionById(currentSubscription.getSubscriptionPaypalId());
            var subscriptionStatus = Utils.getJsonValue(subscriptionPayload, "status", String.class);

            var result = PayPalSubscription.of(currentSubscription.getSubscriptionPaypalId(), subscriptionStatus);

            return ResponseMessageOf.of(HttpStatus.OK, result);
        }

        return ResponseMessageOf.of(HttpStatus.NO_CONTENT);
    }
}
