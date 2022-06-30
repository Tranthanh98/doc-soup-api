CREATE PROCEDURE [dbo].[del_directory]
    @directoryId  bigint
AS
BEGIN
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
    INTO #directory_temp
    FROM directory_temp

    SELECT *
    INTO #file_temp
    FROM (
         SELECT f.id as file_id, f.document_id as document_id
         FROM file_entity as f
         INNER JOIN #directory_temp
         ON #directory_temp.id = f.directory_id) as sub

    -- delete page statistic
    delete p
	from page_statistic p WITH(rowlock)
	INNER JOIN link l
	ON l.id = p.link_id
    INNER JOIN #file_temp f
    ON f.file_id = l.ref_id AND l.document_id is NOT NULL

	-- delete link statistic
	delete st
	from link_statistic st WITH(rowlock)
	INNER JOIN link l
	ON l.id = st.link_id
    INNER JOIN #file_temp f
    ON f.file_id = l.ref_id AND l.document_id is NOT NULL

	-- delete link document
    delete doc
    FROM document doc WITH(rowlock)
    INNER JOIN link l
    ON l.document_id = doc.id
    INNER JOIN #file_temp f
    ON f.file_id = l.ref_id AND l.document_id is NOT NULL

	-- delete link
	delete l
	from link l WITH(rowlock)
    INNER JOIN #file_temp f
    ON f.file_id = l.ref_id AND l.document_id is NOT NULL

	-- delete file document
	delete doc
	from document doc  WITH(rowlock)
	INNER JOIN #file_temp f
    ON doc.id = f.document_id

	-- delete file content
	delete fc
	from file_content fc WITH(rowlock)
	INNER JOIN #file_temp f
	ON fc.id=f.file_id

	-- delete file data-room content
	delete dc
	from data_room_content dc WITH(rowlock)
	INNER JOIN #file_temp f
	ON dc.file_id=f.file_id

	-- delete directory data-room content
	delete dc
	from data_room_content dc WITH(rowlock)
	INNER JOIN #directory_temp d
	ON dc.directory_id=d.id

	-- delete file
	delete f
	from file_entity f WITH(rowlock)
	INNER JOIN #file_temp ft
	ON f.id=ft.file_id

	-- delete directory
    delete d
    from directory_entity d WITH(rowlock)
    INNER JOIN #directory_temp
    ON #directory_temp.id = d.id

    If(OBJECT_ID('tempdb..#file_temp') Is Not Null)
    Begin
    Drop Table #file_temp
    End

	If(OBJECT_ID('tempdb..#directory_temp') Is Not Null)
    Begin
    Drop Table #directory_temp
    End

END
GO
