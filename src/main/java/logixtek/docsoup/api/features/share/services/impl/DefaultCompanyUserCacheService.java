package logixtek.docsoup.api.features.share.services.impl;

import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;
import logixtek.docsoup.api.infrastructure.repositories.CompanyUserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultCompanyUserCacheService implements CompanyUserCacheService {

    private final CompanyUserRepository companyUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(DefaultCompanyUserCacheService.class);

    @Override
    @Cacheable(cacheNames="companyUserEntity", key="#accountId.toString() + '_' +#companyId.toString()")
    public CompanyUserEntity get(String accountId, UUID companyId) {
        logger.info("Trying to get company user for id {} ", accountId);
        var option=  companyUserRepository.findFirstByAccountIdAndCompanyId(accountId, companyId);
        if(option.isPresent())
        {
            return option.get();
        }

        return null;
    }

    @Override
    @CacheEvict(cacheNames="companyUserEntity", allEntries=true, key = "#entity.accountId.toString()+'_'+#entity.companyId.toString()")
    public CompanyUserEntity update(CompanyUserEntity entity) {
        return companyUserRepository.saveAndFlush(entity);
    }
}
