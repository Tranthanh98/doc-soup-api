package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.ReactiveUser;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ReactiveUserHandler")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReactiveUserHandler implements Command.Handler<ReactiveUser, ResponseMessageOf<String>> {
    private  final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;

    @Override
    public ResponseMessageOf<String> handle(ReactiveUser command) {
        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals((!companyOption.getSucceeded())))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        if (command.getTargetAccountId().equals(command.getAccountId())){
            return ResponseMessageOf.ofBadRequest("You cannot reactivate yourself.", Map.of());
        }

        var targetAccount = accountService.get(command.getTargetAccountId());
        if (targetAccount == null) {
            return ResponseMessageOf.ofBadRequest("The user does not exist.", Map.of());
        }

        var targetCompanyUserOption = companyUserRepository.findFirstByAccountIdAndCompanyId(targetAccount.getId(), command.getCompanyId());
        if (targetCompanyUserOption.isPresent()) {
            var targetCompanyUser = targetCompanyUserOption.get();
            if(targetCompanyUser.getStatus().equals(CompanyUserConstant.ACTIVE_STATUS)) {
                return ResponseMessageOf.of(HttpStatus.ACCEPTED);
            }

            if(targetCompanyUser.getStatus().equals(CompanyUserConstant.SUSPENDED_STATUS)) {
                return ResponseMessageOf.ofBadRequest("You cannot reactivate the suspend user.", Map.of());
            }

            if(targetCompanyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
                return ResponseMessageOf.ofBadRequest("You cannot reactivate the owner.", Map.of());
            }

            targetCompanyUser.setStatus(CompanyUserConstant.ACTIVE_STATUS);
            companyUserRepository.saveAndFlush(targetCompanyUser);

            companyUserRepository.reactivateAllLinkByAccountIdAndCompanyId(command.getTargetAccountId(), command.getCompanyId().toString());

            return ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
