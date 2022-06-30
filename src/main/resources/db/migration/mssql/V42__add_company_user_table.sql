CREATE TABLE company_user
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset         NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)            NOT NULL,
    modified_by   varchar(36),
    email         nvarchar(200)          NOT NULL,
    account_id    varchar(36)            NOT NULL,
    company_id    uniqueidentifier,
    CONSTRAINT pk_company_user PRIMARY KEY (id)
)
GO

ALTER TABLE account
    ADD active_company_id uniqueidentifier NOT NULL
GO

ALTER TABLE account
    ADD company_id uniqueidentifier
GO

CREATE NONCLUSTERED INDEX IX_COMPANY_USER_ACCOUNTID ON company_user (account_id)
GO

CREATE NONCLUSTERED INDEX IX_COMPANY_USER_COMPANYID ON company_user (company_id)
GO

CREATE NONCLUSTERED INDEX IX_COMPANY_USER_EMAIL ON company_user (email)
GO

