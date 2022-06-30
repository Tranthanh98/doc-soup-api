package logixtek.docsoup.api.features.emailnotification.payment.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailPaidRemindDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component("SendEmailPaidRemindDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailPaidRemindDomainEventHandler implements Notification.Handler<SendEmailPaidRemindDomainEvent> {
    private final PaymentHistoryRepository _paymentHistoryRepository;
    private final CompanyUserRepository _companyUserRepository;
    private final CompanyRepository _companyRepository;
    private final PlanTierRepository _planTierRepository;

    private final AccountService _accountService;

    private final EmailSenderService _emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailPaidRemindDomainEventHandler.class);

    @Value("${mail.from}")
    private String _mailFrom;

    @Value("${docsoup.client.url}")
    private String _clientUrl;

    @Override
    public void handle(SendEmailPaidRemindDomainEvent notification) {
        var paymentHistoryOption = _paymentHistoryRepository.findById(notification.getPaymentHistoryId());
        if(paymentHistoryOption.isPresent()) {
            var paymentHistory = paymentHistoryOption.get();
            var companyUserOwnerOption = _companyUserRepository.findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNull(paymentHistory.getCompanyId(), CompanyUserConstant.OWNER_TYPE);
            if(companyUserOwnerOption.isPresent()) {
                var companyOption = _companyRepository.findById(companyUserOwnerOption.get().getCompanyId());
                if(companyOption.isPresent()) {
                    var planTierOption = _planTierRepository.findById(companyOption.get().getPlanTierId());
                    if(planTierOption.isPresent()) {
                        var owner = _accountService.get(companyUserOwnerOption.get().getAccountId());

                        var classloader = Thread.currentThread().getContextClassLoader();
                        try (InputStream is = classloader.getResourceAsStream("templates/paymentPaidRemindTemplate.html")) {
                            var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                            IOUtils.closeQuietly(is);

                            if (!Strings.isNullOrEmpty(htmlString)) {
                                var logoImageUrl = _clientUrl + "/img/logo-black.png";

                                var subject = "Your payment information is required for your DocSoup account";
                                var paymentPage = _clientUrl + "/billing";
                                htmlString = htmlString
                                        .replaceAll("@logoImage", logoImageUrl)
                                        .replaceAll("@ownerName", owner.getFirstName() + owner.getLastName())
                                        .replaceAll("@ownerEmail", owner.getEmail())
                                        .replaceAll("@planName", planTierOption.get().getName())
                                        .replaceAll("@paymentPage", paymentPage);

                                _emailSenderService.sendHtmlMessage(_mailFrom, owner.getEmail(), subject, htmlString);
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
