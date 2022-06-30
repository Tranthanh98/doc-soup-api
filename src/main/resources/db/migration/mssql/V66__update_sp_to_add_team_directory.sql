
ALTER PROCEDURE [dbo].[ins_directory_withParent]
    @name NVARCHAR(255) ,
    @parentId  bigint = 0,
    @accountId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER,
    @newId bigint =0 OUTPUT
AS
BEGIN

INSERT INTO directory_entity(parent_id,[level],[name],account_id,created_by,created_date,company_id,is_team)
SELECT @parentId, d.[level] + 1,@name,@accountId,@accountId,GETUTCDATE(),@companyId,d.is_team
FROM directory_entity d where id=@parentId

SELECT @newId = SCOPE_IDENTITY()

END
GO
