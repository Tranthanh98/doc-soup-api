ALTER TABLE account
    ADD phone varchar(20)
GO

CREATE NONCLUSTERED INDEX IX_COMPANY_USER_COMPANYID ON company_user (company_id)
GO
