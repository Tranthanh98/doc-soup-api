CREATE PROCEDURE [dbo].[sel_secure_id_in_directory]
@directoryId  bigint = 0
AS
BEGIN
    SET NOCOUNT ON;
    WITH directory_temp(id)
             as (
            SELECT id
            FROM directory_entity
            WHERE id = @directoryId
            UNION ALL
            SELECT d.id
            FROM directory_temp as a,
                 directory_entity as d
            WHERE a.id = d.parent_id
        )

    SELECT *
    INTO #file_temp
    FROM (
        SELECT f.id as file_id, f.document_id as document_id
        FROM file_entity as f
        INNER JOIN directory_temp
        ON directory_temp.id = f.directory_id) as sub

    CREATE TABLE #result
    (
        secure_id varchar(255)
    )

    INSERT INTO #result
    SELECT *
    FROM (
        SELECT doc.secure_id
        FROM document doc
        INNER JOIN link l
        ON l.document_id = doc.id
        INNER JOIN #file_temp f
        ON f.file_id = l.ref_id AND l.document_id is NOT NULL) as sub

    INSERT
    INTO #result
    SELECT *
    FROM (
         SELECT doc.secure_id
         FROM document doc
         INNER JOIN #file_temp f
         ON doc.id = f.document_id
     ) as sub

    SELECT *
    FROM #result

    If(OBJECT_ID('tempdb..#file_temp') Is Not Null)
    Begin
        Drop Table #file_temp
    End

    If (OBJECT_ID('tempdb..#result') Is Not Null)
    Begin
        Drop Table #result
    End
END
GO