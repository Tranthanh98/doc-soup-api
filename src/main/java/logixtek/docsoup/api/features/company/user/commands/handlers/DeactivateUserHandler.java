package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.DeactivateUser;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("DeactivateUserHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeactivateUserHandler implements Command.Handler<DeactivateUser, ResponseMessageOf<String>>{
    private  final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;
    private final CompanyUserCacheService companyUserCacheService;

    @Override
    public ResponseMessageOf<String> handle(DeactivateUser command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        if (command.getTargetAccountId().equals(command.getAccountId())){
            return ResponseMessageOf.ofBadRequest("You cannot deactivate yourself.", Map.of());
        }

        var targetAccount = accountService.get(command.getTargetAccountId());
        if (targetAccount == null) {
            return ResponseMessageOf.ofBadRequest("The user does not exist.", Map.of());
        }

        var targetCompanyUser = companyUserCacheService.get(targetAccount.getId(), command.getCompanyId());
        if (targetCompanyUser != null) {
            if(targetCompanyUser.getStatus().equals(CompanyUserConstant.DE_ACTIVE_STATUS)) {
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

            if(targetCompanyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                return ResponseMessageOf.ofBadRequest("You cannot deactivate the owner.", Map.of());
            }

            targetCompanyUser.setStatus(CompanyUserConstant.DE_ACTIVE_STATUS);
            companyUserCacheService.update(targetCompanyUser);

            if(targetAccount.getActiveCompanyId().equals(command.getCompanyId())) {
                var firstCompanyUserOption = companyUserRepository
                        .findFirstByAccountIdAndCompanyIdIsNotAndStatusOrderByStatus(targetAccount.getId(), targetCompanyUser.getCompanyId(), CompanyUserConstant.ACTIVE_STATUS);
                if(firstCompanyUserOption.isPresent()) {
                    var firstCompanyUser = firstCompanyUserOption.get();
                    targetAccount.setActiveCompanyId(firstCompanyUser.getCompanyId());
                    accountService.update(targetAccount);
                }
            }

            companyUserRepository.deactivateAllLinkByAccountIdAndCompanyId(command.getTargetAccountId(), command.getCompanyId().toString());

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
