-- Drop the stored procedure if it already exists
IF EXISTS (
        SELECT *
        FROM INFORMATION_SCHEMA.ROUTINES
        WHERE SPECIFIC_SCHEMA = N'dbo'
          AND SPECIFIC_NAME = N'ins_directory_withParent'
          AND ROUTINE_TYPE = N'PROCEDURE'
    )
    DROP PROCEDURE dbo.ins_directory_withParent
GO

CREATE PROCEDURE [dbo].[ins_directory_withParent]
    @name NVARCHAR(255) ,
    @parentId  bigint = 0,
    @accountId NVARCHAR(36),
    @newId bigint =0 OUTPUT
AS
BEGIN

    INSERT INTO directory_entity(parent_id,[level],[name],account_id,created_date,modified_date)
    SELECT @parentId, d.[level] + 1,@name,@accountId,GETUTCDATE(),GETUTCDATE()
    FROM directory_entity d where id=@parentId and account_id=@accountId

    SELECT @newId = SCOPE_IDENTITY()

END

GO
