package logixtek.docsoup.api.features.emailnotification.activity.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.company.user.services.CompanyUserService;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailRemindTakeOwnershipDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SendEmailRemindTakeOwnershipDomainEventHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendEmailRemindTakeOwnershipDomainEventHandler implements Notification.Handler<SendEmailRemindTakeOwnershipDomainEvent> {
    private final CompanyUserService companyUserService;
    private final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;

    private final EncryptService encryptService;
    @Override
    public void handle(SendEmailRemindTakeOwnershipDomainEvent notification) {
        var companyUserOption = companyUserRepository.findById(notification.getRequestMessage().getRemindInvitationCompanyUserId());
        if(companyUserOption.isPresent()) {
            var companyUser = companyUserOption.get();
            var sender = accountService.get(companyUser.getCreatedBy());
            var companyOption = companyRepository.findById(companyUser.getCompanyId());

            if(sender != null && companyOption.isPresent()) {
                var decryptString = encryptService.decrypt(companyUser.getToken());
                var numberOfSend = CompanyUserConstant.INITIAL_NUMBER_OF_SEND;
                if(Strings.isNullOrEmpty(decryptString)) {
                    var tokenUnit = decryptString.split("_");
                    numberOfSend = Integer.parseInt(tokenUnit[2]);
                    numberOfSend += 1;
                }

                var subject = "Take ownership of " + companyOption.get().getName();
                var senderFullName = "";

                var htmlTemplate = "templates/reminderTakeOwnershipOfCompanyTemplate.html";

                companyUserService.inviteUser(companyUser, subject, htmlTemplate, senderFullName, numberOfSend);
            }
        }
    }
}
