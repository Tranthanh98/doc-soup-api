package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import logixtek.docsoup.api.features.company.user.services.CompanyUserService;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailInvitationDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("SendEmailInvitationDomainEventHandler")
@AllArgsConstructor
public class SendEmailInvitationDomainEventHandler implements Notification.Handler<SendEmailInvitationDomainEvent> {
    private final CompanyUserService companyUserService;
    private final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;

    @Override
    public void handle(SendEmailInvitationDomainEvent notification) {
        var companyUserOption = companyUserRepository.findById(notification.getSendEmailInvitationRequest().getCompanyUserId());
        if(companyUserOption.isPresent()) {
            var companyUser = companyUserOption.get();
            var sender = accountService.get(notification.getSendEmailInvitationRequest().getSenderAccountId());
            var subject = "";
            var senderFullName = "";
            if(sender != null) {
                senderFullName = sender.getFirstName() + " " + sender.getLastName();
                subject = senderFullName + " invited you to DocSoup";
            }

            var htmlTemplate = "templates/inviteUserToCompanyTemplate.html";

            companyUserService.inviteUser(companyUser, subject, htmlTemplate, senderFullName, notification.getSendEmailInvitationRequest().getNumberOfSend());
        }
    }
}
