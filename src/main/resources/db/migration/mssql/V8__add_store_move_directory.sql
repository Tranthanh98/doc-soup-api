
-- Create the stored procedure in the specified schema
CREATE PROCEDURE [dbo].[move_directory]
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
                    SET parent_id = @parentId,[level] = 0
                    WHERE id=@id
                END
            ELSE
                BEGIN

                    UPDATE directory_entity WITH(ROWLOCK,UPDLOCK)
                    SET parent_id = @parentId
                    WHERE id=@id

                END

            UPDATE D
            SET D.[level] = P.[level]+1
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
