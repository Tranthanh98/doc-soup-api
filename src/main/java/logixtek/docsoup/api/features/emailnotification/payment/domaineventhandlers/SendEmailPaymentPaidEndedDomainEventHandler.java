package logixtek.docsoup.api.features.emailnotification.payment.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailPaymentPaidEndedDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component("SendEmailPaymentPaidEndedDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@AllArgsConstructor
public class SendEmailPaymentPaidEndedDomainEventHandler implements Notification.Handler<SendEmailPaymentPaidEndedDomainEvent> {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;
    private final PlanTierRepository planTierRepository;

    private final AccountService accountService;

    private final EmailSenderService emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailPaymentPaidEndedDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailPaymentPaidEndedDomainEvent notification) {
        var paymentHistoryOption = paymentHistoryRepository.findById(notification.getPaymentHistoryId());
        if(paymentHistoryOption.isPresent()) {
            var paymentHistory = paymentHistoryOption.get();
            var companyUserOwnerOption = companyUserRepository.findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNull(paymentHistory.getCompanyId(), CompanyUserConstant.OWNER_TYPE);
            if(companyUserOwnerOption.isPresent()) {
                var companyOption = companyRepository.findById(companyUserOwnerOption.get().getCompanyId());
                if(companyOption.isPresent()) {
                    var previousPanTierOption = planTierRepository.findById(notification.getPreviousPlanTierId());
                    var planTierOption = planTierRepository.findById(companyOption.get().getPlanTierId());
                    if(planTierOption.isPresent() && previousPanTierOption.isPresent()) {
                        var owner = accountService.get(companyUserOwnerOption.get().getAccountId());

                        var classloader = Thread.currentThread().getContextClassLoader();
                        try (InputStream is = classloader.getResourceAsStream("templates/paymentPaidEndedTemplate.html")) {
                            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                            IOUtils.closeQuietly(is);

                            if (!Strings.isNullOrEmpty(htmlString)) {
                                var logoImageUrl = clientUrl + "/img/logo-black.png";

                                var subject = "Your DocSoup payment grace period has ended";
                                var paymentPage = clientUrl + "/billing";
                                htmlString = htmlString
                                        .replaceAll("@logoImage", logoImageUrl)
                                        .replaceAll("@ownerName", owner.getFirstName() + owner.getLastName())
                                        .replaceAll("@planName", previousPanTierOption.get().getName())
                                        .replaceAll("@paymentPage", paymentPage);

                                emailSenderService.sendHtmlMessage(mailFrom, owner.getEmail(), subject, htmlString);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
