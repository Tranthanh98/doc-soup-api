package logixtek.docsoup.api.features.share.services;

import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;

import java.util.UUID;

public interface CompanyUserCacheService {

    CompanyUserEntity get(String accountId, UUID companyId);

    CompanyUserEntity update(CompanyUserEntity entity);
}
