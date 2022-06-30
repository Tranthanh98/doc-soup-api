CREATE
NONCLUSTERED INDEX IX_CONTACT_ENTITY_COMPANYID ON contact (company_id)
GO

CREATE
NONCLUSTERED INDEX IX_DIRECTORY_ENTITY_COMPANYID ON directory_entity (company_id)
GO

CREATE
NONCLUSTERED INDEX IX_DIRECTORY_ENTITY_COMPANYID_ACCOUNTID ON directory_entity (company_id, account_id)
GO

DROP INDEX IX_DIRECTORY_ENTITY_ACCOUNTID ON directory_entity
GO

