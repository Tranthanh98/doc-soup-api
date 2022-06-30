package logixtek.docsoup.api.features.share.services;

import logixtek.docsoup.api.infrastructure.entities.AccountEntity;


public interface AccountService {

    AccountEntity get(String accountId);
    AccountEntity update(AccountEntity account);
}
