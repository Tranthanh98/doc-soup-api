package logixtek.docsoup.api.features.company.user.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.company.user.queries.ListCompanyUser;
import logixtek.docsoup.api.infrastructure.models.CompanyUser;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("ListCompanyUserHandler")
@AllArgsConstructor
public class ListCompanyUserHandler implements Command.Handler<ListCompanyUser, ResponseEntity<Collection<CompanyUser>>> {

    private  final CompanyUserRepository companyUserRepository;

    @Override
    public ResponseEntity<Collection<CompanyUser>> handle(ListCompanyUser query) {

        var companyUserOption = companyUserRepository.findFirstByAccountIdAndCompanyId(query.getAccountId(),query.getId());

        if(!companyUserOption.isPresent())
        {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var resultOption = companyUserRepository.findAllCompanyUserByCompanyId(query.getId());

        if(resultOption.isPresent())
        {
           return ResponseEntity.ok(resultOption.get());
        }

        return  ResponseEntity.ok(Collections.emptyList());

    }
}
