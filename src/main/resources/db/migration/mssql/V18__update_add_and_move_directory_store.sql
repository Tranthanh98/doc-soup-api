
GO
ALTER PROCEDURE [dbo].[ins_directory_withParent]
    @name NVARCHAR(255) ,
    @parentId  bigint = 0,
    @accountId NVARCHAR(36),
    @newId bigint =0 OUTPUT
AS
BEGIN

    INSERT INTO directory_entity(parent_id,[level],[name],account_id,created_by,created_date)
    SELECT @parentId, d.[level] + 1,@name,@accountId,@accountId,GETUTCDATE()
    FROM directory_entity d where id=@parentId and account_id=@accountId

    SELECT @newId = SCOPE_IDENTITY()

END
GO

-- Create the stored procedure in the specified schema
ALTER PROCEDURE [dbo].[move_directory]
    @id bigint,
    @parentId bigint,
    @accountId nvarchar(36),
    @error int  OUTPUT

AS
BEGIN

    IF EXISTS (SELECT TOP 1 id FROM directory_entity WHERE id=@id and account_Id = @accountId)
        BEGIN
            IF @parentId  =0
                BEGIN

                    UPDATE directory_entity WITH(ROWLOCK,UPDLOCK)
                    SET parent_id = @parentId,[level] = 0,modified_date=GETUTCDATE(),modified_by=@accountId
                    WHERE id=@id
                END
            ELSE
                BEGIN

                    UPDATE directory_entity WITH(ROWLOCK,UPDLOCK)
                    SET parent_id = @parentId,modified_date=GETUTCDATE(),modified_by=@accountId
                    WHERE id=@id

                END

            UPDATE D
            SET D.[level] = P.[level]+1,modified_date=GETUTCDATE(),modified_by=@accountId
            FROM dbo.directory_entity  AS D WITH(ROWLOCK,UPDLOCK)
                     INNER JOIN dbo.directory_entity AS P
                                ON D.parent_id = P.id and D.account_id = @accountId

            SELECT @error = 202;
        END
    ELSE
        BEGIN
            SELECT @error = 404
            RETURN;
        END
END
GO
