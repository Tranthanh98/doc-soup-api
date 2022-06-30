package logixtek.docsoup.api.features.payment.webhooks.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import com.nimbusds.jose.shaded.json.JSONObject;
import logixtek.docsoup.api.features.payment.constants.PaymentConstant;
import logixtek.docsoup.api.features.payment.services.PaymentGatewayService;
import logixtek.docsoup.api.features.payment.services.SubscriptionService;
import logixtek.docsoup.api.features.payment.webhooks.commands.PaypalSubscriptionWebhook;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.PlanTierConstant;
import logixtek.docsoup.api.infrastructure.entities.PaymentHistoryEntity;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import logixtek.docsoup.api.infrastructure.entities.SubscriptionEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component("PaypalSubscriptionWebhookHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaypalSubscriptionWebhookHandler implements Command.Handler<PaypalSubscriptionWebhook, ResponseEntity<String>> {
    private final PaymentGatewayService paymentGatewayService;
    private final SubscriptionService subscriptionService;
    private final PlanTierRepository planTierRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CompanyUserRepository companyUserRepository;

    @Value("${paypal.webhook.token}")
    String paypalToken;

    @Override
    public ResponseEntity<String> handle(PaypalSubscriptionWebhook command) {
        if (Strings.isNullOrEmpty(command.getToken()) || !command.getToken().equals(paypalToken)) {
            return ResponseEntity.badRequest().build();
        }

        var subscriptionPayload = paymentGatewayService.getSubscriptionById(command.getResource().getId());
        if (Strings.isNullOrEmpty(subscriptionPayload)) {
            return ResponseEntity.badRequest().build();
        }

        var companyId = UUID.fromString(command.getResource().getCustom_id());

        var paypalPlanId = Utils.getJsonValue(subscriptionPayload, "plan_id", String.class);
        var planTierOption = planTierRepository.findByMonthlyPlanPaypalIdOrMonthlyFixedPlanPaypalIdAndIsActiveIsTrue(paypalPlanId, paypalPlanId);
        String subType;
        if (planTierOption.isPresent()) {
            subType = PaymentConstant.MONTHLY;
        } else {
            planTierOption = planTierRepository.findByYearlyPlanPaypalIdOrYearlyFixedPlanPaypalIdAndIsActiveIsTrue(paypalPlanId, paypalPlanId);
            subType = PaymentConstant.YEARLY;
        }

        if (planTierOption.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var planTier = planTierOption.get();

        var paypalSubscriptionId = Utils.getJsonValue(subscriptionPayload, "id", String.class);

        var companyUserOwnerOption =
                companyUserRepository.findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNull(companyId, CompanyUserConstant.OWNER_TYPE);
        if (companyUserOwnerOption.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var companyOwnerAccountId = companyUserOwnerOption.get().getAccountId();
        if (command.getEvent_type().equals(PaymentConstant.BILLING_SUBSCRIPTION_ACTIVATED)
                || command.getEvent_type().equals(PaymentConstant.BILLING_SUBSCRIPTION_UPDATED)) {

            updateSubscription(subscriptionPayload, companyId, paypalPlanId, subType, planTier, paypalSubscriptionId, companyOwnerAccountId);

            updateCompanyPlan(companyId, planTier.getId());

            return ResponseEntity.accepted().build();
        }

        if (command.getEvent_type().equals(PaymentConstant.BILLING_SUBSCRIPTION_CANCELLED)
                || command.getEvent_type().equals(PaymentConstant.BILLING_SUBSCRIPTION_EXPIRED)) {
            var currentSubscriptionOption = subscriptionService.get(companyId);

            if (currentSubscriptionOption.isPresent()) {
                subscriptionService.delete(companyId);
            }

            var limitedTrialPlanTierOption = planTierRepository.findByLevelAndIsActiveIsTrue(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL);

            if (limitedTrialPlanTierOption.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            updateCompanyPlan(companyId, limitedTrialPlanTierOption.get().getId());

            return ResponseEntity.accepted().build();
        }

        if (command.getEvent_type().equals(PaymentConstant.BILLING_SUBSCRIPTION_PAYMENT_FAILED)) {
            createFailedPaymentHistory(command, subscriptionPayload, paypalPlanId,
                    subType, planTier, paypalSubscriptionId, companyId);
        }

        return ResponseEntity.accepted().build();
    }

    private void updateSubscription(String subscriptionPayload,
                                    UUID companyId,
                                    String paypalPlanId,
                                    String subType,
                                    PlanTierEntity planTier,
                                    String paypalSubscriptionId,
                                    String companyOwnerAccountId) {
        var currentSubscriptionOption = subscriptionService.get(companyId);

        SubscriptionEntity subscriptionEntity;
        subscriptionEntity = currentSubscriptionOption.isPresent() ? currentSubscriptionOption.get() : new SubscriptionEntity();

        subscriptionEntity.setModifiedBy(companyOwnerAccountId);
        if (subscriptionEntity.getCreatedBy() == null) {
            subscriptionEntity.setCreatedBy(companyOwnerAccountId);
        }
        subscriptionEntity.setPaypalSubscriptionPayload(subscriptionPayload);
        subscriptionEntity.setCompanyId(companyId);
        subscriptionEntity.setAccountId(companyOwnerAccountId);
        subscriptionEntity.setSubscriptionPaypalId(paypalSubscriptionId);
        subscriptionEntity.setPaypalPlanId(paypalPlanId);
        subscriptionEntity.setSubType(subType);
        subscriptionEntity.setPlanTierId(planTier.getId());

        subscriptionService.updateOrInsert(subscriptionEntity);
    }

    private void createFailedPaymentHistory(PaypalSubscriptionWebhook command, String subscriptionPayload, String paypalPlanId, String subType, PlanTierEntity planTier, String paypalSubscriptionId, UUID companyId) {
        var quantity = Utils.getJsonValue(subscriptionPayload, "quantity", String.class);
        var paymentHistory = new PaymentHistoryEntity();
        paymentHistory.setStatus(PaymentConstant.PAYMENT_FAILED);
        paymentHistory.setQuantity(Integer.parseInt(quantity));
        paymentHistory.setCompanyId(companyId);
        paymentHistory.setPrice(planTier.getSeatPrice());

        if (planTier.getLevel().equals(PlanTierConstant.PLAN_ADVANCED_LEVEL) && (planTier.getMonthlyFixedPlanPaypalId().equals(paypalPlanId) || planTier.getYearlyFixedPlanPaypalId().equals(paypalPlanId))) {
            paymentHistory.setPrice(0.0);
        }

        var initialFee = subType.equals(PaymentConstant.YEARLY) ? planTier.getInitialFee() * PaymentConstant.TOTAL_MONTHS_OF_YEAR : planTier.getInitialFee();
        paymentHistory.setInitialFee(initialFee);

        var discount = subType.equals(PaymentConstant.YEARLY) ? planTier.getYearlyDiscount() : 0;
        paymentHistory.setDiscount(discount);
        var totalAmount = Double.parseDouble(command.getResource().getBilling_info().getLast_failed_payment().getAmount().getValue());
        paymentHistory.setSubTotalAmount(totalAmount);
        paymentHistory.setTotalAmount(totalAmount);
        paymentHistory.setSubscriptionPaypalId(paypalSubscriptionId);
        paymentHistory.setPaypalPlanId(paypalPlanId);
        paymentHistory.setCreatedDate(command.getResource().getBilling_info().getLast_failed_payment().getTime());

        var invoice = generateInvoice(paypalSubscriptionId, planTier, quantity, totalAmount,
                                            command.getResource().getBilling_info().getLast_failed_payment().getTime(), discount, subType);
        paymentHistory.setInvoice(invoice);

        paymentHistoryRepository.saveAndFlush(paymentHistory);
    }


    private void updateCompanyPlan(UUID companyId, Long planTierId) {
        var companyOption = companyRepository.findById(companyId);
        if (companyOption.isPresent()) {
            var company = companyOption.get();
            company.setPlanTierId(planTierId);
            companyRepository.saveAndFlush(company);
        }
    }

    private String generateInvoice(String paypalSubscriptionId, PlanTierEntity planTier, String quantity, double totalAmount, OffsetDateTime create_time, double discount, String subType) {
        JSONObject json = new JSONObject();

        json.put("planTierName", planTier.getName());

        var invoiceId = paypalSubscriptionId;
        json.put("invoiceId", invoiceId);
        json.put("subType", subType);
        json.put("seat", Integer.parseInt(quantity) - planTier.getInitialSeat());

        var totalMonth = subType.equals(PaymentConstant.YEARLY) ? PaymentConstant.TOTAL_MONTHS_OF_YEAR : PaymentConstant.ONE_MONTH;
        var totalInitialFee = (planTier.getInitialFee() - (discount * planTier.getInitialFee())/100) * totalMonth;
        json.put("totalInitialFee", String.format("%.2f", totalInitialFee));

        json.put("initialFee", String.format("%.2f", planTier.getInitialFee()));
        json.put("initialSeat", planTier.getInitialSeat());

        var unitPrice = (planTier.getSeatPrice() - (discount * planTier.getSeatPrice())/100)*totalMonth;
        json.put("unitPrice", String.format("%.2f", unitPrice));

        var totalSeatPrice = (Double.parseDouble(quantity) - planTier.getInitialSeat())*unitPrice;
        json.put("totalSeatPrice", String.format("%.2f", totalSeatPrice));

        json.put("subTotal", String.format("%.2f", totalAmount));
        json.put("total", String.format("%.2f", totalAmount));
        json.put("amountPaid", String.format("%.2f", 0.00));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        var date = create_time.format(formatter);
        json.put("date", date);

        json.put("balanceDue", String.format("%.2f", totalAmount));

        var billFrom = "ePapyrus DocSoup";
        json.put("billFrom", billFrom);

        return json.toJSONString();
    }
}
