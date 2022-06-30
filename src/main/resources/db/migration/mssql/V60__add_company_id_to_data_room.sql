ALTER TABLE data_room
    ADD company_id uniqueidentifier
    GO

BEGIN TRAN

UPDATE d
SET d.company_id = a.active_company_id
    FROM dbo.data_room AS d
INNER JOIN dbo.account AS a
ON d.account_id = a.id

COMMIT TRAN

GO

ALTER TABLE data_room
ALTER
COLUMN company_id uniqueidentifier NOT NULL
GO

