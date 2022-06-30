package logixtek.docsoup.api.features.payment.webhooks.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import com.nimbusds.jose.shaded.json.JSONObject;
import logixtek.docsoup.api.features.payment.constants.PaymentConstant;
import logixtek.docsoup.api.features.payment.services.PaymentGatewayService;
import logixtek.docsoup.api.features.payment.webhooks.commands.PayPalPaymentWebhook;
import logixtek.docsoup.api.infrastructure.constants.PlanTierConstant;
import logixtek.docsoup.api.infrastructure.entities.PaymentHistoryEntity;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component("PaypalPaymentWebhookHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaypalPaymentWebhookHandler implements Command.Handler<PayPalPaymentWebhook, ResponseEntity<String>>  {
    private final PaymentGatewayService paymentGatewayService;
    private final PlanTierRepository planTierRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Value("${paypal.webhook.token}")
    String paypalToken;

    @SneakyThrows
    @Override
    public ResponseEntity<String> handle(PayPalPaymentWebhook command) {
        if(Strings.isNullOrEmpty(command.getToken()) || !command.getToken().equals(paypalToken)) {
            return ResponseEntity.badRequest().build();
        }

        var subscriptionPayload = paymentGatewayService.getSubscriptionById(command.getResource().getBilling_agreement_id());
        if(Strings.isNullOrEmpty(subscriptionPayload)) {
            return ResponseEntity.badRequest().build();
        }

        var companyId = Utils.getJsonValue(subscriptionPayload, "custom_id", String.class);
        var companyOption = companyRepository.findById(UUID.fromString(companyId));
        if(!companyOption.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        var paypalPlanId = Utils.getJsonValue(subscriptionPayload, "plan_id", String.class);


        var paypalPaymentPayload = paymentGatewayService.getPaypalPaymentById(command.getResource().getId());
        if(Strings.isNullOrEmpty(paypalPaymentPayload)) {
            return ResponseEntity.badRequest().build();
        }

        if(command.getEvent_type().equals(PaymentConstant.PAYMENT_SALE_COMPLETED)) {
            var planTierOption = planTierRepository.findByMonthlyPlanPaypalIdOrMonthlyFixedPlanPaypalIdAndIsActiveIsTrue(paypalPlanId, paypalPlanId);
            String subType;
            if(planTierOption.isPresent()) {
                subType = PaymentConstant.MONTHLY;
            } else {
                planTierOption = planTierRepository.findByYearlyPlanPaypalIdOrYearlyFixedPlanPaypalIdAndIsActiveIsTrue(paypalPlanId, paypalPlanId);
                subType = PaymentConstant.YEARLY;
            }


            if(planTierOption.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var planTier = planTierOption.get();

            createSuccessPaymentHistory(companyOption.get().getId(), paypalPlanId, subscriptionPayload,
                    paypalPaymentPayload, planTier, subType);

        }

        return ResponseEntity.accepted().build();
    }

    private void createSuccessPaymentHistory(UUID companyId, String paypalPlanId, String subscriptionPayload, String paypalPaymentPayload, PlanTierEntity planTier, String subType) {
        var paypalPlanPayloadJson = paymentGatewayService.getPlanById(paypalPlanId);
        if(!Strings.isNullOrEmpty(paypalPlanPayloadJson)) {
            var paypalSubscriptionId = Utils.getJsonValue(subscriptionPayload, "id", String.class);
            var quantity = Utils.getJsonValue(subscriptionPayload, "quantity", String.class);

            var amountJsonString = Utils.getJsonValue(paypalPaymentPayload, "amount", String.class);
            var currency = Utils.getJsonValue(amountJsonString, "currency_code", String.class);
            var totalAmount = Double.parseDouble(Utils.getJsonValue(amountJsonString, "value", String.class));

            var create_time_string = Utils.getJsonValue(paypalPaymentPayload, "create_time", String.class);
            var create_time = OffsetDateTime.parse(create_time_string);

            var paymentHistory = new PaymentHistoryEntity();
            paymentHistory.setSubscriptionPaypalId(paypalSubscriptionId);
            paymentHistory.setPaypalPlanId(paypalPlanId);
            paymentHistory.setCompanyId(companyId);
            paymentHistory.setQuantity(Integer.parseInt(quantity));
            paymentHistory.setPrice(planTier.getSeatPrice());

            if(planTier.getLevel().equals(PlanTierConstant.PLAN_ADVANCED_LEVEL) && (planTier.getMonthlyFixedPlanPaypalId().equals(paypalPlanId) || planTier.getYearlyFixedPlanPaypalId().equals(paypalPlanId)))
            {
                paymentHistory.setPrice(0.0);
            }

            var initialFee = subType.equals(PaymentConstant.YEARLY) ? planTier.getInitialFee() * PaymentConstant.TOTAL_MONTHS_OF_YEAR : planTier.getInitialFee();
            paymentHistory.setInitialFee(initialFee);
            var discount = subType.equals(PaymentConstant.YEARLY)? planTier.getYearlyDiscount() : 0;
            paymentHistory.setDiscount(discount);
            paymentHistory.setCurrency(currency);
            paymentHistory.setCreatedDate(create_time);
            paymentHistory.setPayPalPaymentPayload(paypalPaymentPayload);
            paymentHistory.setStatus(PaymentConstant.PAYMENT_PAID);
            paymentHistory.setTotalAmount(totalAmount);
            paymentHistory.setSubTotalAmount(totalAmount);

            var invoice = generateInvoice(paypalPaymentPayload, planTier, quantity, totalAmount, create_time_string, discount, subType);
            paymentHistory.setInvoice(invoice);

            paymentHistoryRepository.saveAndFlush(paymentHistory);
        }
    }


    private String generateInvoice(String paypalPaymentPayload, PlanTierEntity planTier, String quantity, double totalAmount, String create_time_string, double discount, String subType) {
        JSONObject json = new JSONObject();

        json.put("planTierName", planTier.getName());

        var invoiceId = Utils.getJsonValue(paypalPaymentPayload, "id", String.class);
        json.put("invoiceId", invoiceId);
        json.put("subType", subType);

        json.put("initialSeat", planTier.getInitialSeat());

        int totalMonth = subType.equals(PaymentConstant.YEARLY) ? PaymentConstant.TOTAL_MONTHS_OF_YEAR : PaymentConstant.ONE_MONTH;

        var unitPrice = (planTier.getSeatPrice() - (discount * planTier.getSeatPrice())/100)*totalMonth;
        json.put("unitPrice", String.format("%.2f", unitPrice));

        var totalSeatPrice = (Double.parseDouble(quantity) - planTier.getInitialSeat())*unitPrice;
        json.put("totalSeatPrice", String.format("%.2f", totalSeatPrice));

        var initialFee = planTier.getInitialFee() - (discount * planTier.getInitialFee())/100;
         json.put("initialFee", String.format("%.2f", initialFee));

        var totalInitialFee = initialFee * totalMonth;
        json.put("totalInitialFee", String.format("%.2f", totalInitialFee));

        json.put("seat", Integer.parseInt(quantity) - planTier.getInitialSeat());

        json.put("subTotal", String.format("%.2f", totalAmount));
        json.put("total", String.format("%.2f", totalAmount));
        json.put("amountPaid", String.format("%.2f", totalAmount));

        var create_time = OffsetDateTime.parse(create_time_string);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        var date = create_time.format(formatter);
        json.put("date", date);

        var balanceDue = 0.00;
        json.put("balanceDue", String.format("%.2f", balanceDue));

        var billFrom = "ePapyrus DocSoup";
        json.put("billFrom", billFrom);

        return json.toJSONString();
    }
}
