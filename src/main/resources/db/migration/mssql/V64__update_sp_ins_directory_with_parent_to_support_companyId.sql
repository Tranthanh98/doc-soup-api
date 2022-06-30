
ALTER PROCEDURE [dbo].[ins_directory_withParent]
    @name NVARCHAR(255) ,
    @parentId  bigint = 0,
    @accountId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER,
    @newId bigint =0 OUTPUT
AS
BEGIN

INSERT INTO directory_entity(parent_id,[level],[name],account_id,created_by,created_date,company_id)
SELECT @parentId, d.[level] + 1,@name,@accountId,@accountId,GETUTCDATE(),@companyId
FROM directory_entity d where id=@parentId and account_id=@accountId and company_id=@companyId

SELECT @newId = SCOPE_IDENTITY()

END
GO
