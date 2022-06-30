package logixtek.docsoup.api.features.company.user.services;

import logixtek.docsoup.api.infrastructure.entities.CompanyUserEntity;

public interface CompanyUserService {
    void inviteUser(CompanyUserEntity companyUser, String subject, String htmlTemplate, String senderName, Integer numberOfSend);
}
