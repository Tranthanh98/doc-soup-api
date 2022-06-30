ALTER TABLE contact
    ADD company_id uniqueidentifier
    GO


BEGIN TRAN
UPDATE c
SET c.company_id = a.active_company_id
    FROM dbo.contact AS c
INNER JOIN dbo.account AS a
ON c.account_id = a.id

COMMIT TRAN

GO

ALTER TABLE contact
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO
