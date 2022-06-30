
delete from watermark

GO

ALTER TABLE watermark
    ALTER COLUMN image varbinary(MAX) NULL
GO

ALTER TABLE watermark
    ALTER COLUMN text nvarchar(4000) NOT NULL
GO