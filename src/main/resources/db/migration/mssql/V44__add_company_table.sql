CREATE TABLE company
(
    id            uniqueidentifier NOT NULL,
    created_date  datetimeoffset   NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)      NOT NULL,
    modified_by   varchar(36),
    name          nvarchar(200)    NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
)
GO

ALTER TABLE company_user
    ADD member_type int NOT NULL DEFAULT (0)
GO

ALTER TABLE company_user
    ADD status int NOT NULL DEFAULT (1)
GO


DROP INDEX IX_COMPANY_USER_COMPANYID ON company_user
GO

ALTER TABLE account
    DROP COLUMN company_id
GO
