ALTER TABLE link
    ADD company_id uniqueidentifier
    GO

BEGIN TRAN

UPDATE l
SET l.company_id = a.active_company_id
    FROM dbo.link AS l
INNER JOIN dbo.account AS a
ON l.created_by = a.id

COMMIT TRAN

GO
ALTER TABLE link
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO

