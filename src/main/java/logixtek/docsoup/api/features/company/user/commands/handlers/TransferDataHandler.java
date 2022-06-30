package logixtek.docsoup.api.features.company.user.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.features.company.user.commands.TransferData;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("TransferDataHandler")
@AllArgsConstructor
public class TransferDataHandler implements Command.Handler<TransferData, ResponseMessageOf<String>> {
    private  final AccountService accountService;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;

    @Override
    public ResponseMessageOf<String> handle(TransferData command) {
        if(command.getSourceAccountId().equals(command.getDestinationAccountId())) {
            return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        var companyOption = companyService.checkAndGetCompany(command.getCompanyId(),command.getAccountId());

        if(Boolean.TRUE.equals(!companyOption.getSucceeded()))
        {
            return ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));
        }

        if (command.getSourceAccountId().equals(command.getAccountId())){
            return ResponseMessageOf.ofBadRequest("You cannot transfer data to yourself.", Map.of());
        }

        var sourceAccount =  accountService.get(command.getSourceAccountId());
        var destinationAccount = accountService.get(command.getDestinationAccountId());
        if (sourceAccount == null || destinationAccount == null) {
            return ResponseMessageOf.ofBadRequest("The user does not exist.", Map.of());
        }

        var sourceCompanyUser = validateAndGetCompanyUser(sourceAccount.getId(), command.getCompanyId());
        var destinationCompanyUser = validateAndGetCompanyUser(destinationAccount.getId(), command.getCompanyId());
        if(sourceCompanyUser == null || destinationCompanyUser == null) {
            return ResponseMessageOf.of(HttpStatus.BAD_REQUEST);
        }

        if(sourceCompanyUser.getStatus().equals(CompanyUserConstant.ACTIVE_STATUS)) {
            return ResponseMessageOf.ofBadRequest("You can only transfer data from deactivate or suspended user.", Map.of());
        }

        if(sourceCompanyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)) {
            return ResponseMessageOf.ofBadRequest("You cannot transfer data from owner.", Map.of());
        }

        if(sourceCompanyUser.getStatus().equals(CompanyUserConstant.TRANSFERRED)) {
            return ResponseMessageOf.ofBadRequest("This user has been transferred data.", Map.of());
        }

        companyUserRepository.transferDataToAnotherUser(command.getSourceAccountId(), command.getDestinationAccountId(), command.getCompanyId().toString());

        sourceCompanyUser.setStatus(CompanyUserConstant.TRANSFERRED);
        companyUserRepository.saveAndFlush(sourceCompanyUser);

        return ResponseMessageOf.of(HttpStatus.ACCEPTED);
    }

    private CompanyUserEntity validateAndGetCompanyUser(String accountId, UUID companyId) {
        var companyUserOption = companyUserRepository.findFirstByAccountIdAndCompanyId(accountId, companyId);
        if(!companyUserOption.isPresent()) {
            return null;
        }

        return companyUserOption.get();
    }
}
