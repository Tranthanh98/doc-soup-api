package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.AcceptInvitation;
import logixtek.docsoup.api.features.company.user.commands.ResendInvitation;
import logixtek.docsoup.api.features.share.dtos.SendEmailInvitationRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ResendInvitationHandler")
@AllArgsConstructor
public class ResendInvitationHandler implements Command.Handler<ResendInvitation, ResponseMessageOf<Long>> {
    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;
    private final EncryptService encryptService;
    private final JobMessageQueuePublisher publisher;

    private static final Logger logger = LoggerFactory.getLogger(ResendInvitationHandler.class);

    @Override
    public ResponseMessageOf<Long> handle(ResendInvitation command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        var targetCompanyUserOption = companyUserRepository.findById(command.getTargetCompanyUserId());

        if(!targetCompanyUserOption.isPresent()) {
            return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var targetCompanyUser = targetCompanyUserOption.get();
        if(targetCompanyUser.getInvitationStatus().equals(CompanyUserConstant.ACCEPTED_INVITATION) || Strings.isNullOrEmpty(targetCompanyUser.getToken())) {
            return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var decryptString = encryptService.decrypt(targetCompanyUser.getToken());
        if(Strings.isNullOrEmpty(decryptString)) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidInvitation,
                    Map.of(AcceptInvitation.Fields.token, ResponseResource.InvalidInvitation));
        }

        var tokenUnit = decryptString.split("_");

        var numberOfSend = Integer.parseInt(tokenUnit[2]);
        numberOfSend += 1;

        var jobMessage = new JobMessage<SendEmailInvitationRequestMessage>();
        jobMessage.setAction(JobActionConstant.SEND_EMAIL_INVITATION);
        jobMessage.setObjectName(SendEmailInvitationRequestMessage.class.getName());
        var body = SendEmailInvitationRequestMessage.of(command.getAccountId(), targetCompanyUser.getId(), numberOfSend);
        jobMessage.setDataBody(body);

        try {
            publisher.sendMessage(jobMessage);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        targetCompanyUser.setInvitationStatus(CompanyUserConstant.INVITED);

        companyUserRepository.saveAndFlush(targetCompanyUser);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }

}
