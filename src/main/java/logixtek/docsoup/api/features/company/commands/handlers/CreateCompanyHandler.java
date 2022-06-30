package logixtek.docsoup.api.features.company.commands.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.company.commands.CreateCompany;
import logixtek.docsoup.api.features.company.user.services.CompanyUserService;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.PlanTierConstant;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.PlanTierRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("CreateCompanyHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateCompanyHandler implements Command.Handler<CreateCompany, ResponseEntity<String>> {
    private final AccountService accountService;
    private final PlanTierRepository planTierRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyUserService companyUserService;

    @Value("${docsoup.client.url}")
    private String clientUrl;

    @Value("${mail.from}")
    private String mailFrom;

    @Override
    public ResponseEntity<String> handle(CreateCompany command) {
        var account = accountService.get(command.getAccountId());
        if(account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundAccount);
        }

        var planTierOption = planTierRepository.findByLevelAndIsActiveIsTrue(PlanTierConstant.LIMITED_TRIAL_PLAN_LEVEL);

        if(!planTierOption.isPresent()){
            return ResponseEntity.badRequest().body(ResponseResource.NotFoundPlanTier);
        }

        var companyEntity = new CompanyEntity();

        companyEntity.setName(command.getCompanyName());
        companyEntity.setCreatedBy(account.getId());
        companyEntity.setPlanTierId(planTierOption.get().getId());

        companyEntity = companyRepository.saveAndFlush(companyEntity);

        if(Strings.isNullOrEmpty(command.getOwner())) {
            createOwnerCompanyUser(account.getEmail(), account.getId(), companyEntity.getId(), account.getId(), false);
            accountService.update(account);

            return ResponseEntity.status(HttpStatus.CREATED).body(companyEntity.getId().toString());
        }

        var invitedOwnerCompanyUser = createOwnerCompanyUser(command.getOwner(), null, companyEntity.getId(), account.getId(), true);
        createOwnerCompanyUser(account.getEmail(), account.getId(), companyEntity.getId(), account.getId(), false);


        var senderName = account.getFirstName() + " " + account.getLastName();

        sendEmailToOwner(invitedOwnerCompanyUser, companyEntity.getName(), senderName);

        return ResponseEntity.status(HttpStatus.CREATED).body(companyEntity.getId().toString());
    }

    private CompanyUserEntity createOwnerCompanyUser(String email, String accountId, UUID companyId, String createdBy, Boolean isInvited) {
        var companyUserEntity = new CompanyUserEntity();
        companyUserEntity.setCompanyId(companyId);
        companyUserEntity.setCreatedBy(createdBy);
        companyUserEntity.setEmail(email);
        companyUserEntity.setRole(RoleDefinition.C_ADMIN);
        companyUserEntity.setMember_type(CompanyUserConstant.OWNER_TYPE);
        if(Boolean.TRUE.equals(isInvited)) {
            companyUserEntity.setAccountId(null);
            companyUserEntity.setStatus(CompanyUserConstant.SUSPENDED_STATUS);
            companyUserEntity.setInvitationStatus(CompanyUserConstant.INVITED);
        } else {
            companyUserEntity.setAccountId(accountId);
            companyUserEntity.setInvitationStatus(CompanyUserConstant.ACCEPTED_INVITATION);

        }

        return companyUserRepository.saveAndFlush(companyUserEntity);
    }

    @SneakyThrows
    private void sendEmailToOwner(CompanyUserEntity companyUser, String companyName, String senderName) {
        var subject = companyName + " on DocSoup has been provisioned for you";
        var htmlTemplate = "templates/takeOwnerShipTemplate.html";

        companyUserService.inviteUser(companyUser, subject, htmlTemplate, senderName, CompanyUserConstant.INITIAL_NUMBER_OF_SEND);
    }

}
