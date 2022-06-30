package logixtek.docsoup.api.features.payment.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.payment.commands.UpgradeDowngrade;
import logixtek.docsoup.api.features.payment.constants.PaymentConstant;
import logixtek.docsoup.api.features.payment.services.PaymentGatewayService;
import logixtek.docsoup.api.features.payment.services.SubscriptionService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.PlanTierConstant;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("UpgradeDowngradeHandler")
@AllArgsConstructor
public class UpgradeDowngradeHandler implements Command.Handler<UpgradeDowngrade, ResponseEntity<String>> {
    private final PaymentGatewayService paymentGatewayService;
    private final CompanyRepository companyRepository;
    private final PlanTierRepository planTierRepository;
    private final CompanyUserCacheService companyUserCacheService;
    private final SubscriptionService subscriptionService;
    private final CompanyUserRepository companyUserRepository;

    @Override
    public ResponseEntity<String> handle(UpgradeDowngrade command) {
        var companyUser = companyUserCacheService.get(command.getAccountId(), command.getCompanyId());

        if (companyUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResource.NotFoundCompany);
        }

        if (companyUser.getMember_type() != CompanyUserConstant.OWNER_TYPE) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResource.DonNotHavePermission);
        }

        var companyOption = companyRepository.findById(command.getCompanyId());
        if (!companyOption.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseResource.NotFoundCompany);
        }

        var planTierOption = planTierRepository.findByIdAndIsActiveIsTrue(command.getPlanTierId());
        if (!planTierOption.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseResource.InvalidPlanTier);
        }

        var planTier = planTierOption.get();

        if (!planTier.getLevel().equals(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL) &&
                !planTier.getMonthlyPlanPaypalId().equals(command.getPaypalPlanId()) &&
                !planTier.getYearlyPlanPaypalId().equals(command.getPaypalPlanId()) &&
                planTier.getMonthlyFixedPlanPaypalId() != null &&
                !planTier.getMonthlyFixedPlanPaypalId().equals(command.getPaypalPlanId()) &&
                planTier.getYearlyFixedPlanPaypalId() != null &&
                !planTier.getYearlyFixedPlanPaypalId().equals(command.getPaypalPlanId())
        ) {
            return ResponseEntity.badRequest().build();
        }

        var company = companyOption.get();

        var companySubscriptionOption = subscriptionService.get(company.getId());

        if (!companySubscriptionOption.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseResource.NotFoundCompanySubscription);
        }

        var companySubscription = companySubscriptionOption.get();

        var payPalSubscription = paymentGatewayService.getSubscriptionById(companySubscription.getSubscriptionPaypalId());

        if(payPalSubscription == null ) {
            return ResponseEntity.badRequest().body(ResponseResource.InvalidPaypalSubscription);
        }

        var payPalSubStatus = Utils.getJsonValue(payPalSubscription, "status", String.class);

        if(!payPalSubStatus.equals(PaymentConstant.PAYPAL_STATUS_ACTIVE))
        {
            return ResponseEntity.badRequest().body(ResponseResource.InvalidPaypalSubscription);
        }

        if (planTier.getLevel().equals(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL) && Strings.isNullOrEmpty(command.getPaypalPlanId())) {
            var canceledPaypalSubscriptionResult = paymentGatewayService.cancelSubscription(companySubscription.getSubscriptionPaypalId());
            if (!canceledPaypalSubscriptionResult) {
                return ResponseEntity.badRequest().body(ResponseResource.NotAbleCancelPaypalSubscription);
            }
        } else {
            var totalUsers = companyUserRepository.countAllByCompanyIdAndStatusAndAccountIdIsNotNull(company.getId(), CompanyUserConstant.ACTIVE_STATUS);
            String paypalPlanId = command.getSubscriptionType().equals(PaymentConstant.YEARLY) ? planTier.getYearlyPlanPaypalId() : planTier.getMonthlyPlanPaypalId();
            var payPalQuantity = totalUsers;
            if(planTier.getLevel().equals(PlanTierConstant.PLAN_ADVANCED_LEVEL))
            {
                if(totalUsers <= planTier.getInitialSeat()) {
                    paypalPlanId = command.getSubscriptionType().equals(PaymentConstant.YEARLY) ? planTier.getYearlyFixedPlanPaypalId() : planTier.getMonthlyFixedPlanPaypalId();
                    payPalQuantity = 1;
                }
            }

            var result = paymentGatewayService.updatePlanIdAndQuantitySubscription(companySubscription.getSubscriptionPaypalId(), paypalPlanId, payPalQuantity.toString());
            if(!result.getSucceeded()) {
                return ResponseEntity.badRequest().body(ResponseResource.NotAbleUpgradePaypalSubscription);
            }

            var approveLink = result.getData().stream().filter(x -> x.getRel().equals(PaymentConstant.PAYPAL_APPROVE_LINK)).findFirst();
            if(approveLink.isPresent()) {
                return ResponseEntity.ok(approveLink.get().getHref());
            }
        }

        return ResponseEntity.accepted().build();
    }
}

