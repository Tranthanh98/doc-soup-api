package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.company.user.commands.AcceptInvitation;
import logixtek.docsoup.api.features.share.dtos.SendEmailAcceptedInvitationRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import logixtek.docsoup.api.infrastructure.services.EncryptService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component("AcceptInvitationHandler")
@AllArgsConstructor
public class AcceptInvitationHandler implements Command.Handler<AcceptInvitation, ResponseMessageOf<Long>> {
    private final CompanyUserRepository companyUserRepository;
    private final EncryptService encryptService;

    private final Logger logger = LoggerFactory.getLogger(AcceptInvitationHandler.class);

    @Override
    public ResponseMessageOf<Long> handle(AcceptInvitation command) {
        if(!Strings.isNullOrEmpty(command.getToken())) {
            var decryptString = encryptService.decrypt(command.getToken());
            if(Strings.isNullOrEmpty(decryptString)) {
                return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidInvitation,
                        Map.of(AcceptInvitation.Fields.token, ResponseResource.InvalidInvitation));
            }

            var tokenUnit = decryptString.split("_");

            var invitationCompanyUserId = Long.parseLong(tokenUnit[1]);
            var invitationCompanyUserOption = companyUserRepository.findById(invitationCompanyUserId);
            if(invitationCompanyUserOption.isPresent()) {
                var invitationCompanyUser = invitationCompanyUserOption.get();
                if(!Strings.isNullOrEmpty(invitationCompanyUser.getAccountId())) {
                    return ResponseMessageOf.of(HttpStatus.ACCEPTED);
                }

                var token = command.getToken().replaceAll(" ", "+");
                if(token.equals(invitationCompanyUser.getToken())) {
                    var acceptationInvitationStatus = Boolean.TRUE.equals(command.getIsAccepted())
                            ? CompanyUserConstant.ACCEPTED_INVITATION : CompanyUserConstant.REJECT_INVITATION;

                    invitationCompanyUser.setInvitationStatus(acceptationInvitationStatus);
                    companyUserRepository.saveAndFlush(invitationCompanyUser);

                    if(!command.getIsAccepted()) {
                        invitationCompanyUser.setRejectDate(OffsetDateTime.now(ZoneOffset.UTC));
                    }

                    companyUserRepository.saveAndFlush(invitationCompanyUser);

                    return ResponseMessageOf.of(HttpStatus.ACCEPTED);
                }

                return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidInvitation,
                        Map.of(AcceptInvitation.Fields.token, ResponseResource.InvalidInvitation));
            }
        }

        return ResponseMessageOf.ofBadRequest(ResponseResource.InvalidInvitation,
                Map.of(AcceptInvitation.Fields.token, ResponseResource.InvalidInvitation));
    }
}
