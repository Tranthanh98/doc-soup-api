package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.UnsuspendUser;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UnsuspendUserHandler")
@AllArgsConstructor
public class UnsuspendUserHandler implements Command.Handler<UnsuspendUser, ResponseMessageOf<String>> {
    private  final AccountService accountService;
    private final CompanyService companyService;
    private final CompanyUserCacheService companyUserCacheService;

    @Override
    public ResponseMessageOf<String> handle(UnsuspendUser command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        if (command.getTargetAccountId().equals(command.getAccountId())){
            return ResponseMessageOf.ofBadRequest("You cannot unsuspend yourself.", Map.of());
        }

        var targetAccount = accountService.get(command.getTargetAccountId());
        if (targetAccount == null) {
            return ResponseMessageOf.ofBadRequest("The user does not exist.", Map.of());
        }

        var targetCompanyUser = companyUserCacheService.get(targetAccount.getId(), command.getCompanyId());
        if (targetCompanyUser != null) {
            if(targetCompanyUser.getStatus().equals(CompanyUserConstant.ACTIVE_STATUS)) {
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

            if(targetCompanyUser.getStatus().equals(CompanyUserConstant.DE_ACTIVE_STATUS)) {
                return ResponseMessageOf.ofBadRequest("You cannot unsuspend the deactivate user.", Map.of());
            }

            if(targetCompanyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                return ResponseMessageOf.ofBadRequest("You cannot unsuspend the owner.", Map.of());
            }

            targetCompanyUser.setStatus(CompanyUserConstant.ACTIVE_STATUS);
            companyUserCacheService.update(targetCompanyUser);

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }
        return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
