package logixtek.docsoup.api.features.payment.billinginfo.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.billinginfo.commands.CreateOrEditBillingInfo;
import logixtek.docsoup.api.features.payment.billinginfo.mappers.BillingInfoMapper;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("CreateOrEditBillingInfoHandler")
@AllArgsConstructor
public class CreateOrEditBillingInfoHandler implements Command.Handler<CreateOrEditBillingInfo, ResponseEntity<String>> {
    private final CompanyRepository companyRepository;
    private final CompanyUserCacheService companyService;

    @Override
    public ResponseEntity<String> handle(CreateOrEditBillingInfo command) {
        var companyUser = companyService.get(command.getAccountId(), command.getCompanyId());

        if(companyUser == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResource.NotFoundCompany);
        }

        if(!companyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResource.DonNotHavePermission);
        }

        var companyOption = companyRepository.findById(command.getCompanyId());

        if(!companyOption.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        var company = companyOption.get();

        company = BillingInfoMapper.INSTANCE.updateToEntity(company, command);

        companyRepository.saveAndFlush(company);

        return ResponseEntity.accepted().build();
    }
}
