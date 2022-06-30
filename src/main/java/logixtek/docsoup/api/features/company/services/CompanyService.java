package logixtek.docsoup.api.features.company.services;

import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.models.ResultOf;

import java.util.UUID;

public interface CompanyService {

    ResultOf<CompanyEntity> checkAndGetCompany(UUID companyId, String accountId);
}
