package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailAcceptedInvitationDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
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

@Component("SendEmailAcceptedInvitationDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailAcceptedInvitationDomainEventHandler implements Notification.Handler<SendEmailAcceptedInvitationDomainEvent> {
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;
    private final AccountService accountService;
    private final EmailSenderService emailSenderService;

    private static final Logger logger = LoggerFactory.getLogger(SendEmailAcceptedInvitationDomainEventHandler.class);

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Override
    public void handle(SendEmailAcceptedInvitationDomainEvent notification) {
        var acceptedInvitationCompanyUserOption = companyUserRepository.findById(notification.getRequestMessage().getAcceptedInvitationCompanyUserId());
        if(acceptedInvitationCompanyUserOption.isPresent()) {
            var acceptedInvitationCompanyUser = acceptedInvitationCompanyUserOption.get();
            var companyOption = companyRepository.findById(acceptedInvitationCompanyUser.getCompanyId());
            var inviterAccount = accountService.get(acceptedInvitationCompanyUser.getCreatedBy());
            var invitedAccount = accountService.get(acceptedInvitationCompanyUser.getAccountId());
            if(companyOption.isPresent() && inviterAccount != null && invitedAccount != null) {
                var classloader = Thread.currentThread().getContextClassLoader();
                try (InputStream is = classloader.getResourceAsStream("templates/userAcceptInvitationToCompanyTemplate.html")) {

                    var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
                    IOUtils.closeQuietly(is);

                    if (!Strings.isNullOrEmpty(htmlString)) {
                        var logoImageUrl = clientUrl + "/img/logo-black.png";
                        var teamUrl = clientUrl + "/settings";

                        var subject = acceptedInvitationCompanyUser.getEmail() + " successfully joined the DocSoup team";
                        htmlString = htmlString
                                .replaceAll("@logoImage", logoImageUrl)
                                .replaceAll("@companyName", companyOption.get().getName())
                                .replaceAll("@invitedName", invitedAccount.getFirstName() + " " + invitedAccount.getLastName())
                                .replaceAll("@inviterName", inviterAccount.getFirstName() + " " + inviterAccount.getLastName())
                                .replaceAll("@invitationEmail", acceptedInvitationCompanyUser.getEmail())
                                .replaceAll("@teamUrl", teamUrl);

                        emailSenderService.sendHtmlMessage(mailFrom, inviterAccount.getEmail(), subject, htmlString);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
