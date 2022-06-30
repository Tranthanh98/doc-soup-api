package logixtek.docsoup.api.features.checkin.commands.handlers;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import com.fasterxml.jackson.core.JsonProcessingException;
import logixtek.docsoup.api.features.checkin.commands.CheckInCommand;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.domainevents.CreateDirectoryDomainEvent;
import logixtek.docsoup.api.features.share.dtos.SendEmailAcceptedInvitationRequestMessage;
import logixtek.docsoup.api.features.share.dtos.SendEmailVisitedDocumentSampleRequestMessage;
import logixtek.docsoup.api.features.share.publishers.JobMessageQueuePublisher;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.*;
import logixtek.docsoup.api.infrastructure.entities.AccountEntity;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.entities.PlanTierEntity;
import logixtek.docsoup.api.infrastructure.repositories.AccountRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import logixtek.docsoup.api.infrastructure.repositories.FileRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.models.JobMessage;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;

@Component("CheckInCommandHandler")
@AllArgsConstructor
public class CheckInCommandHandler implements Command.Handler<CheckInCommand, ResponseEntity<String>> {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyRepository companyRepository;
    private final PlanTierRepository planTierRepository;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;
    
    private final JobMessageQueuePublisher publisher;
    private final Pipeline pipeline;

    private final Logger logger = LoggerFactory.getLogger(CheckInCommandHandler.class);

    @Override
    public ResponseEntity<String> handle(CheckInCommand checkInCommand) {

        var getAccountFromDB = accountRepository.findById(checkInCommand.getAccountId());

        var account = checkInCommand.getAccount();

        ensureAccountInfo(account);

        if(getAccountFromDB.isPresent())
        {
            var existingAccount = getAccountFromDB.get();

            var companyUserOption = companyUserRepository.findFirstByAccountIdAndCompanyId(existingAccount.getId(), existingAccount.getActiveCompanyId());
            if(!companyUserOption.isPresent()) {
                return ResponseEntity.badRequest().body("{\"message\" : \""+ResponseResource.NotFoundYourCompany+"\" }");
            }

            var activeCompanyUser = companyUserOption.get();
            if(!activeCompanyUser.getStatus().equals(CompanyUserConstant.ACTIVE_STATUS)) {
                return ResponseEntity.badRequest().body("{\"message\" : \" "+ResponseResource.ContactWithAdministrator+"\", \"status\": " + activeCompanyUser.getStatus() + " }");
            }

            existingAccount.setLastName(account.getLastName());
            existingAccount.setFirstName(account.getFirstName());
            existingAccount.setEmail(account.getEmail());
            existingAccount.setCheckInTime(Instant.now());

            accountRepository.saveAndFlush(existingAccount);

            checkAndAcceptInvitations(existingAccount);

            var myFolderOption = directoryRepository
                    .findFirstByAccountIdAndCompanyIdAndIsTeamFalse(account.getId(), account.getActiveCompanyId());

            if(!myFolderOption.isPresent()){
                raiseCreateDirectoryDomainEvent(existingAccount, false);
            }

            raiseUploadDefaultFile(existingAccount);

            return ResponseEntity.accepted().build();
        }

        var planTierOption = planTierRepository.findByLevelAndIsActiveIsTrue(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL);

        if(!planTierOption.isPresent()){
           return ResponseEntity.badRequest().body(ResponseResource.NotFoundPlanTier);
        }

        account = checkAndCreateAccountWithCompany(account, planTierOption);

        sendVisitedDocumentSampleEmail(account.getId());

        checkAndAcceptInvitation(account);

        checkAndCreateDefaultFolder(account);

        raiseUploadDefaultFile(account);

        return  ResponseEntity.accepted().build();
    }

    private void checkAndAcceptInvitations(AccountEntity existingAccount) {
        var companyUsersOption = companyUserRepository
                .findAllCompanyUserByEmailAndInvitationStatusAndAccountIdIsNull(existingAccount.getEmail(), CompanyUserConstant.ACCEPTED_INVITATION);
        if(companyUsersOption.isPresent()) {
            var companyUsers = companyUsersOption.get();
            var ownerToMemberCompanyUsers = new ArrayList<CompanyUserEntity>();

            var jobMessages = new ArrayList<JobMessage>();
            companyUsers.forEach(companyUser -> {
                if(companyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                    var ownerToMemberCompanyUser = changeOwnerToMember(companyUser.getCompanyId(), existingAccount.getId());
                    if(ownerToMemberCompanyUser != null) {
                        ownerToMemberCompanyUsers.add(ownerToMemberCompanyUser);
                    }
                } else {
                    var jobMessage = new JobMessage<SendEmailAcceptedInvitationRequestMessage>();
                    jobMessage.setAction(JobActionConstant.SEND_EMAIL_ACCEPTED_INVITATION);
                    jobMessage.setObjectName(SendEmailAcceptedInvitationRequestMessage.class.getName());
                    var body = SendEmailAcceptedInvitationRequestMessage.of(companyUser.getId());
                    jobMessage.setDataBody(body);

                    jobMessages.add(jobMessage);
                }
                companyUser.setAccountId(existingAccount.getId());
                companyUser.setStatus(CompanyUserConstant.ACTIVE_STATUS);
            });
            companyUserRepository.saveAllAndFlush(companyUsers);
            companyUserRepository.saveAllAndFlush(ownerToMemberCompanyUsers);

            publisher.sendMessageBatch(jobMessages);
        }
    }

    @NotNull
    private AccountEntity checkAndCreateAccountWithCompany(AccountEntity account, Optional<PlanTierEntity> planTierOption) {
        var companyEntity = new CompanyEntity();

        companyEntity.setName(account.getEmail());

        companyEntity.setCreatedBy(account.getId());

        companyEntity.setPlanTierId(planTierOption.get().getId());

        companyEntity = companyRepository.saveAndFlush(companyEntity);

        account.setActiveCompanyId(companyEntity.getId());

        account = accountService.update(account);

        var companyUserEntity = new CompanyUserEntity();
        companyUserEntity.setCompanyId(companyEntity.getId());
        companyUserEntity.setAccountId(account.getId());
        companyUserEntity.setCreatedBy(account.getId());
        companyUserEntity.setEmail(account.getEmail());
        companyUserEntity.setRole(RoleDefinition.C_ADMIN);

        companyUserRepository.saveAndFlush(companyUserEntity);
        return account;
    }

    private void checkAndAcceptInvitation(AccountEntity account) {
        var companyUserOption = companyUserRepository
                .findFirstByEmailAndInvitationStatusAndAccountIdIsNull(account.getEmail(), CompanyUserConstant.ACCEPTED_INVITATION);

        if(companyUserOption.isPresent())
        {
            var companyUser = companyUserOption.get();
            account.setActiveCompanyId(companyUser.getCompanyId());

            if(companyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                var ownerToMemberCompanyUser = changeOwnerToMember(companyUser.getCompanyId(), account.getId());
                if(ownerToMemberCompanyUser != null) {
                    companyUserRepository.saveAndFlush(ownerToMemberCompanyUser);
                }
            }

            account = accountService.update(account);

            companyUser.setAccountId(account.getId());
            companyUser.setStatus(CompanyUserConstant.ACTIVE_STATUS);
            companyUserRepository.saveAndFlush(companyUser);

            try {
                sendEmailToInviter(companyUser.getId());   
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private void sendEmailToInviter(Long invitationCompanyUserId) throws JsonProcessingException {
        var jobMessage = new JobMessage<SendEmailAcceptedInvitationRequestMessage>();
        jobMessage.setAction(JobActionConstant.SEND_EMAIL_ACCEPTED_INVITATION);
        jobMessage.setObjectName(SendEmailAcceptedInvitationRequestMessage.class.getName());
        var body = SendEmailAcceptedInvitationRequestMessage.of(invitationCompanyUserId);
        jobMessage.setDataBody(body);
        publisher.sendMessage(jobMessage);
    }

    private  void ensureAccountInfo(AccountEntity account)
    {
        if(account.getLastName()==null) {

            account.setFirstName("Mr.DocSoup");

        }

        if(account.getLastName()==null)
        {
            var number = Instant.now().getNano();
            account.setLastName(String.valueOf(number));
        }

        if(account.getEmail()==null)
        {
            account.setEmail(account.getFirstName()+account.getLastName()+"@docsoup.com");
        }

        account.setCheckInTime(Instant.now());
    }

    private void checkAndCreateDefaultFolder(AccountEntity account){
        var teamFolderOption = directoryRepository.findFirstByAccountIdAndCompanyIdAndIsTeamTrue(account.getId(), account.getActiveCompanyId());

        if(!teamFolderOption.isPresent()){
            raiseCreateDirectoryDomainEvent(account, true);
        }

        var myFolderOption = directoryRepository.findFirstByAccountIdAndCompanyIdAndIsTeamFalse(account.getId(), account.getActiveCompanyId());

        if(!myFolderOption.isPresent()){
            raiseCreateDirectoryDomainEvent(account, false);
        }
    }

    private void raiseCreateDirectoryDomainEvent(AccountEntity account, boolean isTeam){
        var domainEvent = CreateDirectoryDomainEvent.of(ContentConstant.rootParentId,
                isTeam ? ContentConstant.TEAM_FOLDER_DEFAULT_NAME : ContentConstant.MY_FOLDER_DEFAULT_NAME,
                isTeam,
                account.getId(),
                account.getActiveCompanyId());

        domainEvent.send(pipeline);
    }

    private CompanyUserEntity changeOwnerToMember(UUID companyId, String currentAccountId) {
        var ownerCompanyUserOption = companyUserRepository.findFirstByCompanyIdAndMemberTypeAndAccountIdIsNotNullAndAccountIdNotEqual(companyId, CompanyUserConstant.OWNER_TYPE, currentAccountId);
        if(ownerCompanyUserOption.isPresent()) {
            var ownerCompanyUser = ownerCompanyUserOption.get();
            ownerCompanyUser.setMember_type(CompanyUserConstant.INVITED_TYPE);
            return ownerCompanyUser;
        }

        return null;
    }

    private void raiseUploadDefaultFile(AccountEntity account) {
        var allFiles = fileRepository.countAllByCompanyId(account.getActiveCompanyId());

        if(allFiles == 0){
            var classloader = Thread.currentThread().getContextClassLoader();
            var fileStream = classloader.getResourceAsStream("sample-PDF.pdf");

            try{

                var commandUpload = new UploadFileCommand();

                MultipartFile multipartFile = new MockMultipartFile("sample-pdf.pdf", "sample-pdf.pdf", "application/pdf", fileStream);

                commandUpload.setMultipartFile(multipartFile);
                commandUpload.setDisplayName("sample PDF.pdf");
                commandUpload.setNda(false);
                commandUpload.setCompanyId(account.getActiveCompanyId());
                commandUpload.setAccountId(account.getId());

                var result = commandUpload.execute(pipeline);

                if(!result.getSucceeded()){
                    logger.error(result.getMessage());
                }
            }
            catch (IOException ex){
                logger.error(ex.getMessage());
            }
        }
    }

    private void sendVisitedDocumentSampleEmail(String accountId) {
        try {
            var jobMessage = new JobMessage<SendEmailVisitedDocumentSampleRequestMessage>();
            jobMessage.setAction(JobActionConstant.SEND_EMAIL_VISITED_DOCUMENT_SAMPLE);
            jobMessage.setObjectName(SendEmailVisitedDocumentSampleRequestMessage.class.getName());
            var requestMessage = SendEmailVisitedDocumentSampleRequestMessage.of(accountId);
            jobMessage.setDataBody(requestMessage);
            publisher.sendMessage(jobMessage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
