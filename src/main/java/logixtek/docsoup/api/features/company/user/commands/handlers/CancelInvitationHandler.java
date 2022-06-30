package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.CancelInvitation;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("CancelInvitationHandler")
@AllArgsConstructor
public class CancelInvitationHandler implements Command.Handler<CancelInvitation, ResponseMessageOf<String>> {
    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;

    @Override
    public ResponseMessageOf<String> handle(CancelInvitation command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        var targetCompanyUserOption = companyUserRepository.findById(command.getTargetCompanyUserId());

        if(!targetCompanyUserOption.isPresent()) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.UserNotInvited, Map.of());
        }

        var targetCompanyUser = targetCompanyUserOption.get();
        if(targetCompanyUser.getInvitationStatus().equals(CompanyUserConstant.ACCEPTED_INVITATION)) {
            return ResponseMessageOf.ofBadRequest(ResponseResource.CannotCancelInvitation, Map.of());
        }
        
        companyUserRepository.delete(targetCompanyUser);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }
}
