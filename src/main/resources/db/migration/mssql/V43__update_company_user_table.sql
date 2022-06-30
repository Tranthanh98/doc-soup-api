ALTER TABLE company_user
    ALTER COLUMN account_id varchar(36) NULL
GO

DROP INDEX company_user.IX_COMPANY_USER_COMPANYID
GO

ALTER TABLE company_user
    ALTER COLUMN company_id uniqueidentifier NOT NULL
GO

CREATE NONCLUSTERED INDEX IX_COMPANY_USER_COMPANYID ON company_user (company_id)
GO
