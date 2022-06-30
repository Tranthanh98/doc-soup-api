package logixtek.docsoup.api.features.payment.services;

import logixtek.docsoup.api.features.payment.models.PaypalHATEOASLink;
import logixtek.docsoup.api.infrastructure.models.ResultOf;

import java.util.List;

public interface PaymentGatewayService {
    String getSubscriptionById(String subscriptionId);

    ResultOf<List<PaypalHATEOASLink>> updatePlanIdAndQuantitySubscription(String subscriptionId, String planId, String quantity);

    boolean cancelSubscription(String subscriptionId);

    String getPaypalPaymentById(String paymentId);

    String getPlanById(String planId);
}
