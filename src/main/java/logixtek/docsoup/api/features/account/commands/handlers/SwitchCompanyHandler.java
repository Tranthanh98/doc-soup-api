package logixtek.docsoup.api.features.account.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.account.commands.SwitchCompany;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("SwitchCompanyHandler")
@AllArgsConstructor
public class SwitchCompanyHandler implements Command.Handler<SwitchCompany, ResponseEntity<String>> {
    private final AccountService accountService;
    private final CompanyUserCacheService companyUserCacheService;
    private final CompanyRepository companyRepository;

    @Override
    public ResponseEntity<String> handle(SwitchCompany command) {
        if(command.getCompanyId().equals(command.getDestinationCompanyId())) {
            return ResponseEntity.accepted().build();
        }

        var companyOption = companyRepository.findById(command.getCompanyId());
        if(!companyOption.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundCompany);
        }

        var companyUser = companyUserCacheService.get(command.getAccountId(), command.getDestinationCompanyId());
        if(companyUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotBelongCompany);
        }

        var account = accountService.get(command.getAccountId());
        if(account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundAccount);
        }

        account.setActiveCompanyId(command.getDestinationCompanyId());

        accountService.update(account);

        return ResponseEntity.accepted().build();
    }
}
