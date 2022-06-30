GO
ALTER TABLE link
ADD status int
GO

UPDATE link
SET status = CONVERT(int, [disabled])

ALTER TABLE link
ALTER
COLUMN status int NOT NULL
GO

DECLARE @ConstraintName nvarchar(200)
SELECT @ConstraintName = Name FROM SYS.DEFAULT_CONSTRAINTS
WHERE PARENT_OBJECT_ID = OBJECT_ID('link')
  AND PARENT_COLUMN_ID = (SELECT column_id FROM sys.columns
                          WHERE NAME = N'disabled'
                            AND object_id = OBJECT_ID(N'link'))
    IF @ConstraintName IS NOT NULL
EXEC ('ALTER TABLE link DROP CONSTRAINT ' + @ConstraintName)

ALTER TABLE link
DROP COLUMN disabled
GO