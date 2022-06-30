ALTER TABLE link_accounts
    ADD temp_name nvarchar(150);
GO

UPDATE link_accounts
SET temp_name = (SELECT SUBSTRING (name, 1, 150));
GO

ALTER TABLE link_accounts
ALTER COLUMN temp_name nvarchar(150) NOT NULL;

DROP INDEX link_accounts.IX_LINK_ACCOUNTS_NAME_COMPANY_ID;

ALTER TABLE link_accounts
    DROP COLUMN name;

EXEC sp_rename 'link_accounts.temp_name', 'name', 'COLUMN';

CREATE NONCLUSTERED INDEX IX_LINK_ACCOUNTS_NAME_COMPANY_ID ON link_accounts (name, company_id)
GO