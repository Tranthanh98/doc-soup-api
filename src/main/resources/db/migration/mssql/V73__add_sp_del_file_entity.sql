
CREATE PROCEDURE [dbo].[del_file_entity]
    @id bigint
AS
BEGIN



-- delete page statistic
delete p
from page_statistic p WITH(rowlock)
INNER JOIN link l WITH(nolock)
ON l.id = p.link_id
WHERE l.ref_id=@id and l.document_id is NOT NULL

-- delete statistic

delete st
from link_statistic st WITH(rowlock)
INNER JOIN link l WITH(nolock)
ON l.id = st.link_id
WHERE l.ref_id=@id and l.document_id is NOT NULL

-- delete link document

delete doc
from document doc  WITH(rowlock)
INNER JOIN link l
ON doc.id = l.document_id
WHERE l.ref_id=@id and l.document_id is NOT NULL

-- delete link

delete l
from link l WITH(rowlock)
WHERE l.ref_id=@id and l.document_id is NOT NULL

-- delete file document

delete doc
from document doc  WITH(rowlock)
INNER JOIN file_entity f
ON doc.id = f.document_id
WHERE f.id=@id

-- delete file content

delete fc from file_content fc WITH(rowlock)
 where fc.id=@id

-- delete file

delete f from file_entity f WITH(rowlock) WHERE f.id=@id

END
GO


