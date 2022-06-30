
CREATE PROCEDURE [dbo].[del_data_room]
    @id bigint
AS
BEGIN

-- delete page statistic
delete p
from page_statistic p WITH(rowlock)
INNER JOIN link sl WITH(nolock)
ON sl.id = p.link_id
inner join link l WITH(nolock)
ON sl.parent = l.id
WHERE l.ref_id=@id and l.document_id is NULL

-- delete statistic

delete st
from link_statistic st WITH(rowlock)
INNER JOIN link sl WITH(nolock)
ON sl.id = st.link_id
inner join link l WITH(nolock)
ON sl.parent = l.id
WHERE l.ref_id=@id and l.document_id is NULL

-- delete document

delete doc
from document doc  WITH(rowlock)
INNER JOIN link sl
ON doc.id = sl.document_id
inner join link l WITH(nolock)
ON sl.parent = l.id
WHERE l.ref_id=@id and l.document_id is NULL

-- delete sub link

delete sl
from link sl WITH(rowlock)
inner join link l WITH(nolock)
ON sl.parent = l.id
WHERE l.ref_id=@id  and l.document_id is NULL

-- delete link

delete l
from link l WITH(rowlock)
WHERE l.ref_id=@id and l.document_id is NULL

-- delete data room content

delete dc from data_room_content dc WITH(rowlock)
 where dc.data_room_id=@id

-- delete data room

delete d from data_room d WITH(rowlock)
 WHERE d.id=@id

END
GO

