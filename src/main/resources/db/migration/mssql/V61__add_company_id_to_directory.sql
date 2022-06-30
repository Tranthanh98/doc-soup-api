ALTER TABLE directory_entity
    ADD company_id uniqueidentifier
    GO

BEGIN TRAN

UPDATE d
SET d.company_id = a.active_company_id
    FROM dbo.directory_entity AS d
INNER JOIN dbo.account AS a
ON d.account_id = a.id

COMMIT TRAN

GO

ALTER TABLE directory_entity
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO

