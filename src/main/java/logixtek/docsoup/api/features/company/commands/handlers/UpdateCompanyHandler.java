package logixtek.docsoup.api.features.company.commands.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.commands.UpdateCompany;
import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("UpdateCompanyHandler")
@AllArgsConstructor
public class UpdateCompanyHandler implements Command.Handler<UpdateCompany, ResponseMessageOf<String>> {

    private  final CompanyRepository companyRepository;
    private  final CompanyService companyService;

    @Override
    public ResponseMessageOf<String> handle(UpdateCompany command) {

        var companyOption = companyService.checkAndGetCompany(command.getId(),command.getAccountId());

        if(Boolean.TRUE.equals(companyOption.getSucceeded()))
        {
            var company = companyOption.getData();
            company.setName(command.getName());
            company.setTrackingOwnerVisit(command.getTrackingOwnerVisit());
            companyRepository.saveAndFlush(company);

            return  ResponseMessageOf.of(HttpStatus.ACCEPTED);
        }

        return   ResponseMessageOf.ofBadRequest(companyOption.getMessage(), Map.of("id",companyOption.getMessage()));

    }
}
