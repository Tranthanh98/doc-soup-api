
-- Create the stored procedure in the specified schema
ALTER PROCEDURE [dbo].[move_directory]
    @id bigint,
    @parentId bigint,
    @accountId nvarchar(36),
    @companyId UNIQUEIDENTIFIER,
    @isTeam bit,
    @error int  OUTPUT

AS
BEGIN

IF EXISTS (SELECT TOP 1 id FROM directory_entity WHERE id=@id and company_id = @companyId)
BEGIN
            IF @parentId  =0
BEGIN

UPDATE directory_entity WITH(ROWLOCK,UPDLOCK)
SET parent_id = @parentId,
    [level] = 0,
    modified_date=GETUTCDATE(),
    modified_by=@accountId,
    is_team=@isTeam,
    account_id=@accountId
WHERE id=@id
END
ELSE
BEGIN

UPDATE directory_entity WITH(ROWLOCK,UPDLOCK)
SET parent_id = @parentId,
    modified_date=GETUTCDATE(),
    modified_by=@accountId,
    is_team=@isTeam,
    account_id=@accountId
WHERE id=@id

END

UPDATE D
SET D.[level] = P.[level]+1,
    modified_date=GETUTCDATE(),
    modified_by=@accountId,
    is_team=p.is_team,
    account_id=p.account_id
    FROM dbo.directory_entity  AS D WITH(ROWLOCK,UPDLOCK)
    INNER JOIN dbo.directory_entity AS P
ON D.parent_id = P.id and D.company_id = @companyId

SELECT @error = 202;
END
ELSE
BEGIN
SELECT @error = 404
           RETURN;
END

END
GO
