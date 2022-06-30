ALTER PROCEDURE [dbo].[del_directory]
    @directoryId  bigint,
    @errorCode INT = 0 OUTPUT
AS
BEGIN
    SET NOCOUNT ON

    SET @errorCode = 0;

    DECLARE @directory_temp table (
        id bigint
    );

    DECLARE @file_temp table (
        file_id bigint,
        document_id UNIQUEIDENTIFIER
    );

    WITH directory_temp(id) as (
        SELECT id
        FROM directory_entity
        WHERE id = @directoryId
        UNION ALL
        SELECT d.id
        FROM directory_temp as a,
             directory_entity as d
        WHERE a.id = d.parent_id
    )

    INSERT INTO @directory_temp
    SELECT id FROM directory_temp

    IF EXISTS (SELECT TOP(1) d.id FROM @directory_temp AS d
        JOIN file_entity AS f ON f.directory_id = d.id
        WHERE f.nda = 1
    )
        BEGIN
            SET @errorCode = -999;
            RETURN;
        END

    BEGIN TRAN

    BEGIN TRY

        INSERT INTO @file_temp
        SELECT f.id as file_id, f.document_id as document_id
         FROM file_entity as f
         INNER JOIN @directory_temp d
         ON d.id = f.directory_id

-- delete page statistic
        delete p
        from page_statistic p WITH(rowlock)
        INNER JOIN link l
        ON l.id = p.link_id
        INNER JOIN @file_temp f
        ON f.file_id = l.ref_id AND l.document_id is NOT NULL

        -- delete link statistic
        delete st
        from link_statistic st WITH(rowlock)
        INNER JOIN link l
        ON l.id = st.link_id
        INNER JOIN @file_temp f
        ON f.file_id = l.ref_id AND l.document_id is NOT NULL

        -- delete link document
        delete doc
        FROM document doc WITH(rowlock)
        INNER JOIN link l
        ON l.document_id = doc.id
        INNER JOIN @file_temp f
        ON f.file_id = l.ref_id AND l.document_id is NOT NULL

        -- delete link
        delete l
        from link l WITH(rowlock)
        INNER JOIN @file_temp f
        ON f.file_id = l.ref_id AND l.document_id is NOT NULL

        -- delete file document
        delete doc
        from document doc  WITH(rowlock)
        INNER JOIN @file_temp f
        ON doc.id = f.document_id

        -- delete file content
        delete fc
        from file_content fc WITH(rowlock)
        INNER JOIN @file_temp f
        ON fc.id=f.file_id

        -- delete file data-room content
        delete dc
        from data_room_content dc WITH(rowlock)
        INNER JOIN @file_temp f
        ON dc.file_id=f.file_id

        -- delete directory data-room content
        delete dc
        from data_room_content dc WITH(rowlock)
        INNER JOIN @directory_temp d
        ON dc.directory_id=d.id

        -- delete file
        delete f
        from file_entity f WITH(rowlock)
        INNER JOIN @file_temp ft
        ON f.id=ft.file_id

        -- delete directory
        delete d
        from directory_entity d WITH(rowlock)
        INNER JOIN @directory_temp d1
        ON d1.id = d.id


    COMMIT TRAN

    END TRY

    BEGIN CATCH
        SET @errorCode = ERROR_NUMBER();

        ROLLBACK TRAN
    END CATCH
END
GO
