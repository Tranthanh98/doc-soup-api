package logixtek.docsoup.api.features.company.services.impl;

import logixtek.docsoup.api.features.company.services.CompanyService;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultCompanyService implements CompanyService {

    private  final CompanyRepository companyRepository;
    private  final CompanyUserRepository companyUserRepository;
    @Override
    public ResultOf<CompanyEntity> checkAndGetCompany(UUID companyId, String accountId) {

        final var memberType=1;
        var companyOption = companyRepository.findById(companyId);

        if(!companyOption.isPresent())
        {
            return  ResultOf.of(false,"Not found company");
        }

        var companyUserOption = companyUserRepository.findFirstByAccountIdAndCompanyId(accountId,companyId);

        if(!companyUserOption.isPresent())
        {
            return  ResultOf.of(false, ResponseResource.NotBelongCompany);
        }

        var companyUser = companyUserOption.get();
        if(companyUser.getMember_type() == memberType && !companyUser.getRole().equals(RoleDefinition.C_ADMIN))
        {
            return  ResultOf.of(false, ResponseResource.DonNotHavePermission);
        }

        if(companyUser.getStatus()!=1)
        {
            return  ResultOf.of(false, ResponseResource.AccountWasSuspend);
        }

        return  ResultOf.of(companyOption.get());
    }
}
