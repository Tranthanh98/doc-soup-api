GO ALTER PROCEDURE [dbo].[sel_file_by_keyword]
    @keyword VARCHAR (255),
    @companyId UNIQUEIDENTIFIER,
    @accountId VARCHAR (36),
    @page INT,
    @pageSize INT
AS
BEGIN

    DECLARE @totalRows INT = (SELECT COUNT(fe.id) FROM file_entity AS fe
    LEFT JOIN directory_entity AS de ON de.id = fe.directory_id
    WHERE fe.display_name LIKE CONCAT('%', @keyword, '%') AND fe.company_id = @companyId
    AND ( de.is_team = 1 OR fe.created_by = @accountId OR fe.modified_by = @accountId )
    )

    DECLARE @AllFileId TABLE (id bigint);

INSERT INTO @AllFileId SELECT fe.id FROM file_entity AS fe
                                             LEFT JOIN directory_entity AS de ON de.id = fe.directory_id
WHERE fe.display_name LIKE CONCAT('%', @keyword, '%') AND fe.company_id = @companyId
  AND ( de.is_team = 1 OR fe.created_by = @accountId OR fe.modified_by = @accountId )
ORDER BY fe.id DESC OFFSET (@page * @pageSize)  ROWS FETCH NEXT @pageSize ROWS ONLY

DECLARE @isFile BIT = 1

    ;WITH cte (file_id, file_name, id, name, parent_id, is_team, updateDate, owner)
                      AS (
        SELECT f.id, f.name, d1.id, d1.name, d1.parent_id, d1.is_team ,CONVERT(DATETIME2, ISNULL(f.modified_date, f.created_date), 1) AS updateDate,
               CONCAT(ac.first_name, ' ', ac.last_name) AS owner
        FROM directory_entity AS d1 WITH (NOLOCK)
                 JOIN file_entity AS f WITH (NOLOCK) ON f.directory_id = d1.id
                 JOIN account AS ac WITH (NOLOCK) ON ac.id = f.account_id
     WHERE f.id IN (SELECT id FROM @AllFileId)

     UNION ALL

SELECT c.file_id, c.file_name, d2.id, d2.name, d2.parent_id, d2.is_team, c.updateDate, c.owner
FROM directory_entity AS d2 WITH (NOLOCK), cte AS c
WHERE c.parent_id = d2.id
  AND d2.parent_id IS NOT NULL
    )
SELECT file_id AS id, file_name AS displayName, is_team AS isTeam, STRING_AGG(name, '/') WITHIN GROUP (ORDER BY id) AS locationPath, updateDate, owner,
            @totalRows AS totalRows, @isFile AS isFile FROM cte
GROUP BY file_id, file_name, is_team, updateDate, owner

END