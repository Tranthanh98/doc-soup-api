package logixtek.docsoup.api.features.company.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.queries.GetActiveCompany;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.models.CompanyOfUser;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("GetActiveCompanyHandler")
@AllArgsConstructor
public class GetActiveCompanyHandler implements Command.Handler<GetActiveCompany, ResponseMessageOf<CompanyOfUser>> {

    private final CompanyRepository companyRepository;

    @Override
    public ResponseMessageOf<CompanyOfUser> handle(GetActiveCompany query) {

        var companyOption = companyRepository
                .findByIdAndAccountIdAndUserStatus(query.getCompanyId(),query.getAccountId(),CompanyUserConstant.ACTIVE_STATUS);

        if(companyOption.isPresent()){
            return ResponseMessageOf.of(HttpStatus.OK, companyOption.get());
        }

        return ResponseMessageOf.of(HttpStatus.NOT_FOUND);
    }
}
