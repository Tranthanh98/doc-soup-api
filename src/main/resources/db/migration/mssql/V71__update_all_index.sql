CREATE
NONCLUSTERED INDEX IX_FILE_ENTITY_COMPANYID_ACCOUNTID ON file_entity (company_id, account_id)
GO

CREATE
NONCLUSTERED INDEX IX_FILE_ENTITY_COMPANYID_DIRECTORYID ON file_entity (company_id, directory_id)
GO

CREATE
NONCLUSTERED INDEX IX_WATERMARK_COMPANYID_ACCOUNTID ON watermark (company_id, account_id)
GO

DROP INDEX IX_FILE_ENTITY_ACCOUNTID ON file_entity
GO

DROP INDEX IX_FILE_ENTITY_DIRECTORYID ON file_entity
GO

DROP INDEX IX_FILE_ENTITY_DISPLAYNAME ON file_entity
GO

DROP INDEX IX_WATERMARK_ACCOUNTID ON watermark
GO

