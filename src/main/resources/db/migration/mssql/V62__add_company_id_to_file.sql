ALTER TABLE file_entity
    ADD company_id uniqueidentifier
    GO

BEGIN TRAN

UPDATE f
SET f.company_id = a.active_company_id
    FROM dbo.file_entity AS f
INNER JOIN dbo.account AS a
ON f.account_id = a.id

COMMIT TRAN

GO

ALTER TABLE file_entity
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO

