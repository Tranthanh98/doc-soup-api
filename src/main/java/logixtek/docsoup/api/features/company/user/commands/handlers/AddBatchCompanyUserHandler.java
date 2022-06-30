package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.AddBatchCompanyUser;
import logixtek.docsoup.api.features.share.dtos.SendEmailInvitationRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.JobActionConstant;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("AddBatchCompanyUserHandler")
@AllArgsConstructor
public class AddBatchCompanyUserHandler implements Command.Handler<AddBatchCompanyUser, ResponseMessageOf<List<Long>>> {

    private final CompanyService companyService;
    private final CompanyUserRepository companyUserRepository;
    private final JobMessageQueuePublisher publisher;

    private static final int INVITED_TYPE = 1;
    private static final int SUSPEND_STATUS  = -1;

    @Override
    public ResponseMessageOf<List<Long>> handle(AddBatchCompanyUser command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        var companyUsersOption = companyUserRepository.findAllByEmailInAndCompanyId(command.getEmails(), command.getCompanyId());

        var existedCompanyUserEmails = new ArrayList<String>();

        if(companyUsersOption.isPresent()) {
            existedCompanyUserEmails.addAll(companyUsersOption.get().stream().map(CompanyUserEntity::getEmail).collect(Collectors.toList()));
        }

        var companyUsers = new ArrayList<CompanyUserEntity>();
        if(command.getEmails().isEmpty()) {
            return  ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        command.getEmails().forEach(email -> {
            if(!existedCompanyUserEmails.contains(email)) {
                var companyUser = new CompanyUserEntity();
                companyUser.setCompanyId(command.getCompanyId());
                companyUser.setEmail(email);
                companyUser.setCreatedBy(command.getAccountId());
                companyUser.setMember_type(INVITED_TYPE);
                companyUser.setRole(command.getRole());
                companyUser.setStatus(SUSPEND_STATUS);
                companyUser.setInvitationStatus(CompanyUserConstant.INVITED);
                companyUsers.add(companyUser);
            }
        });


        var createdCompanyUsers = companyUserRepository.saveAllAndFlush(companyUsers);

        List<JobMessage> queueMessages = new ArrayList<>();
        createdCompanyUsers.forEach(item -> {
            var jobMessage = new JobMessage<SendEmailInvitationRequestMessage>();
            jobMessage.setAction(JobActionConstant.SEND_EMAIL_INVITATION);
            jobMessage.setObjectName(SendEmailInvitationRequestMessage.class.getName());
            var body = SendEmailInvitationRequestMessage.of(item.getCreatedBy(), item.getId(), CompanyUserConstant.INITIAL_NUMBER_OF_SEND);
            jobMessage.setDataBody(body);
            queueMessages.add(jobMessage);
        });

        publisher.sendMessageBatch(queueMessages);

        var createdIds = createdCompanyUsers.stream().map(CompanyUserEntity::getId).collect(Collectors.toList());

        return  ResponseMessageOf.of(HttpStatus.CREATED, createdIds);
    }
}
