ALTER PROCEDURE [dbo].[sel_user_statistic_of_company]
    @accountId VARCHAR (36),
    @companyId UNIQUEIDENTIFIER,
    @numOfDay INTEGER
AS
BEGIN
    DECLARE @fileTable TABLE(
        fileId bigint,
        fileName VARCHAR(255),
        displayName VARCHAR(255),
        directoryId bigint,
        links bigint
    )

    INSERT INTO @fileTable SELECT fe.id, fe.name, fe.display_name, fe.directory_id,
                                  (
                                      SELECT count(DISTINCT l.id)
                                      FROM link l
                                               LEFT JOIN link_statistic ls   on l.id = ls.link_id
                                      WHERE l.ref_id = fe.id and l.document_id is not null
                                        and l.created_date BETWEEN DateAdd(DD, -@numOfDay, CAST(GETDATE() as date)) and GETDATE()
                                  ) AS links
                           FROM file_entity fe where fe.account_id = @accountId and fe.company_id = @companyId
                           ORDER BY fe.created_by DESC

DECLARE @tempTable TABLE (
        fileId bigint,
        fileName VARCHAR(255),
        displayName VARCHAR(255),
        directoryId bigint,
        links bigint,
        data_room_id bigint
    ) ;

with temp(file_id, directory_id ) as (
    SELECT ft.fileId,ft.directoryId   from @fileTable ft
    UNION ALL
    select c.file_id,  d.parent_id from directory_entity as d with(nolock), temp as c
where d.id = c.directory_id
    )
INSERT into @tempTable  select ft.*, dtr.data_room_id FROM temp t
                                                               LEFT JOIN @fileTable ft on t.file_id = ft.fileId
                                                               LEFT JOIN data_room_content dtr on dtr.directory_id = t.directory_id or dtr.file_id = t.file_id

SELECT fileId, fileName, displayName, links,count(distinct data_room_id) as dataRoomNumberOfFile
from @tempTable
GROUP BY fileId, fileName, displayName, links
END
GO
