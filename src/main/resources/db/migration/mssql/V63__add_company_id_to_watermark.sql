ALTER TABLE watermark
    ADD company_id uniqueidentifier
    GO

BEGIN TRAN

UPDATE w
SET w.company_id = a.active_company_id
    FROM dbo.watermark AS w
INNER JOIN dbo.account AS a
ON w.account_id = a.id

COMMIT TRAN

GO

ALTER TABLE watermark
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO

