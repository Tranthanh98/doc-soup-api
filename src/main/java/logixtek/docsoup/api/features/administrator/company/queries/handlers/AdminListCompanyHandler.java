package logixtek.docsoup.api.features.administrator.company.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.company.queries.AdminListCompany;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@AllArgsConstructor
@Component("AdminListCompanyHandler")
public class AdminListCompanyHandler implements Command.Handler<AdminListCompany, ResponseEntity<Collection<CompanyEntity>>> {

    private final CompanyRepository companyRepository;

    @Override
    public ResponseEntity<Collection<CompanyEntity>> handle(AdminListCompany query) {

        var listCompanies = companyRepository.findAll();

        return ResponseEntity.ok(listCompanies);
    }
}
