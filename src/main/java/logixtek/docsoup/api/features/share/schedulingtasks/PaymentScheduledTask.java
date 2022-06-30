package logixtek.docsoup.api.features.share.schedulingtasks;

import logixtek.docsoup.api.features.payment.constants.PaymentConstant;
import logixtek.docsoup.api.features.share.dtos.SendEmailPaidRemindRequestMessage;
import logixtek.docsoup.api.features.share.dtos.SendEmailPaymentPaidEndedRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.constants.PlanTierConstant;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.repositories.SubscriptionEntityRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;

@Component("PaymentScheduledTask")
@AllArgsConstructor
public class PaymentScheduledTask {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final SubscriptionEntityRepository subscriptionEntityRepository;
    private final CompanyRepository companyRepository;
    private final PlanTierRepository planTierRepository;

    private final int ONE_DAY = 1;
    private final int THREE_DAY = 3;

    JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(PaymentScheduledTask.class);
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void paymentSubscriptionFailed() {
        var subscriptions = subscriptionEntityRepository.findAll();
        subscriptions.forEach(subscription -> {
            var billingInfoPayload = Utils.getJsonValue(subscription.getPaypalSubscriptionPayload(), "billing_info", String.class);

            var nextBillingTimeString = Utils.getJsonValue(billingInfoPayload, "next_billing_time", String.class);

            var nextBillingTime = OffsetDateTime.parse(nextBillingTimeString);

            if(nextBillingTime.isBefore(OffsetDateTime.now(ZoneOffset.UTC)))  {
                var failedPaymentHistoryOption = paymentHistoryRepository
                        .findFirstBySubscriptionPaypalIdOrderByCreatedDateDesc(subscription.getSubscriptionPaypalId());
                if(failedPaymentHistoryOption.isPresent()) {
                    var failedPaymentHistory = failedPaymentHistoryOption.get();

                    var createdDateWithDateOnly = failedPaymentHistory
                            .getCreatedDate()
                            .withOffsetSameInstant(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS);

                    var now = OffsetDateTime.now(ZoneOffset.UTC)
                            .withOffsetSameInstant(ZoneOffset.UTC)
                            .truncatedTo(ChronoUnit.DAYS);

                    if(failedPaymentHistory.getStatus().equals(PaymentConstant.PAYMENT_FAILED) &&
                            failedPaymentHistory.getCreatedDate().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
                        if(ChronoUnit.DAYS.between(createdDateWithDateOnly, now) == ONE_DAY) {
                            var jobMessage = new JobMessage<SendEmailPaidRemindRequestMessage>();
                            jobMessage.setObjectName(SendEmailPaidRemindRequestMessage.class.getName());
                            jobMessage.setAction(JobActionConstant.SEND_EMAIL_PAID_REMIND);
                            jobMessage.setDataBody(SendEmailPaidRemindRequestMessage.of(failedPaymentHistory.getId()));
                            try {
                                publisher.sendMessage(jobMessage);
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }

                        if(ChronoUnit.DAYS.between(createdDateWithDateOnly, now) == THREE_DAY) {
                            var companyOption = companyRepository.findById(failedPaymentHistory.getCompanyId());
                            if(companyOption.isPresent()) {
                                long previousPlanTierId = companyOption.get().getPlanTierId();
                                downgradeThePlan(companyOption.get());

                                var jobMessage = new JobMessage<SendEmailPaymentPaidEndedRequestMessage>();
                                jobMessage.setObjectName(SendEmailPaymentPaidEndedRequestMessage.class.getName());
                                jobMessage.setAction(JobActionConstant.SEND_EMAIL_PAID_ENDED);
                                jobMessage.setDataBody(SendEmailPaymentPaidEndedRequestMessage.of(failedPaymentHistory.getId(), previousPlanTierId));
                                try {
                                    publisher.sendMessage(jobMessage);
                                } catch (Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }

        });
    }

    private void downgradeThePlan(CompanyEntity company) {
        var limitedPlanTierOption = planTierRepository.findByLevelAndIsActiveIsTrue(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL);
        if(limitedPlanTierOption.isPresent()) {
            company.setPlanTierId(limitedPlanTierOption.get().getId());
            companyRepository.saveAndFlush(company);
        }
    }
}
