package logixtek.docsoup.api.features.company.user.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.user.queries.GetListCompanyOfUser;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.models.CompanyOfUser;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("GetListCompanyOfUserHandler")
@AllArgsConstructor
public class GetListCompanyOfUserHandler implements Command.Handler<GetListCompanyOfUser, ResponseEntity<Collection<CompanyOfUser>>> {

    private final CompanyRepository companyRepository;

    @Override
    public ResponseEntity<Collection<CompanyOfUser>> handle(GetListCompanyOfUser query) {

        var companiesOfUser = companyRepository.findAllByAccountIdAndCompanyUserStatus(query.getAccountId(), CompanyUserConstant.ACTIVE_STATUS);

        if(companiesOfUser.isPresent()){
            var result = companiesOfUser.get();
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(Collections.emptyList());
    }
}
