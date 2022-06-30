DROP PROCEDURE [dbo].[sel_children_of_parentId_by_accountId]
GO
CREATE PROCEDURE [dbo].[sel_children_of_parentId]
    @parentId bigint
AS
BEGIN
WITH cte AS
         (
             SELECT d1.id, d1.parent_id, d1.name
             FROM directory_entity  AS d1 WITH (NOLOCK)
WHERE id = @parentId
UNION ALL
SELECT d2.id, d2.parent_id, d2.Name
FROM directory_entity AS d2 WITH (NOLOCK) JOIN cte AS c ON d2.parent_id = c.id
    )
SELECT id
FROM cte
END
GO
