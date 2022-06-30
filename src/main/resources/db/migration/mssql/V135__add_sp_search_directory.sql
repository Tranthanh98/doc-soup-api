GO
CREATE PROCEDURE [dbo].[sel_directory_by_keyword]
    @keyword VARCHAR (255),
    @companyId UNIQUEIDENTIFIER,
    @accountId VARCHAR (36),
    @page INT,
    @pageSize INT
AS
BEGIN

    DECLARE
@totalRows INT = (SELECT COUNT(de.id) FROM directory_entity de
    WHERE de.name LIKE CONCAT('%', @keyword, '%') AND de.company_id = @companyId
    AND ( de.is_team = 1 OR de.created_by = @accountId OR de.modified_by = @accountId )
    )

    DECLARE
@AllDirectoryIds TABLE (id bigint);

INSERT INTO @AllDirectoryIds
SELECT de.id
FROM directory_entity AS de
WHERE de.name LIKE CONCAT('%', @keyword, '%')
  AND de.company_id = @companyId
  AND (de.is_team = 1 OR de.created_by = @accountId OR de.modified_by = @accountId)
ORDER BY de.id DESC
OFFSET (@page * @pageSize) ROWS FETCH NEXT @pageSize ROWS ONLY

DECLARE
@isFile BIT = 0

    ;
WITH cte (directory_id, directory_name, id, parent_name, parent_id, is_team, updateDate, owner)
         AS (
        SELECT d.id,
               d.name,
               dp.id,
               dp.name,
               dp.parent_id,
               dp.is_team,
               CONVERT(DATETIME2, ISNULL(d.modified_date, d.created_date), 1) AS updateDate,
               CONCAT(ac.first_name, ' ', ac.last_name)                       AS owner
        FROM directory_entity AS dp
WITH (NOLOCK)
    JOIN directory_entity AS d
WITH (NOLOCK)
ON d.parent_id = dp.id
    JOIN account AS ac
WITH (NOLOCK)
ON ac.id = d.account_id
WHERE d.id IN (SELECT id FROM @AllDirectoryIds)

UNION ALL

SELECT c.directory_id,
       c.directory_name,
       d.id,
       d.name,
       d.parent_id,
       d.is_team,
       c.updateDate,
       c.owner
FROM directory_entity AS d WITH (NOLOCK), cte AS c
WHERE c.parent_id = d.id
  AND d.parent_id IS NOT NULL
    )
SELECT directory_id   AS            id,
       directory_name AS            displayName,
       is_team        AS            isTeam,
       STRING_AGG(parent_name, '/') WITHIN GROUP (ORDER BY id) AS locationPath, updateDate, owner,
            @totalRows AS totalRows, @isFile AS isFile
FROM cte
GROUP BY directory_id, directory_name, is_team, updateDate, owner

END