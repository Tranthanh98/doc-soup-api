GO
ALTER PROCEDURE
[dbo].[sel_directory_by_keyword]
    @keyword VARCHAR (255),
    @companyId UNIQUEIDENTIFIER,
    @accountId VARCHAR (36),
    @page INT,
    @pageSize INT
AS
BEGIN

        DECLARE
@totalRows INT = (SELECT COUNT(de.id) FROM directory_entity de WITH (NOLOCK)
    WHERE de.name LIKE CONCAT('%', @keyword, '%') AND de.company_id = @companyId
    AND ( de.is_team = 1 OR de.created_by = @accountId OR de.modified_by = @accountId )
    )

DECLARE
@AllDirectories TABLE (id bigint, displayName nvarchar(256), parent_id bigint, isTeam bit, updateDate DATETIME2, accountId nvarchar(36));

INSERT INTO @AllDirectories
SELECT de.id,
       de.name,
       de.parent_id,
       de.is_team,
       CONVERT(DATETIME2, ISNULL(de.modified_date, de.created_date), 1) AS updateDate,
       de.account_id                                                    as accountId
FROM directory_entity AS de
WHERE de.name LIKE CONCAT('%', @keyword, '%')
  AND de.company_id = @companyId
  AND (de.is_team = 1 OR de.created_by = @accountId OR de.modified_by = @accountId)
ORDER BY de.id DESC
OFFSET (@page * @pageSize) ROWS FETCH NEXT @pageSize ROWS ONLY

DECLARE
@isFile BIT = 0

DECLARE
@result TABLE (id bigint, displayName nvarchar(256), isTeam bit, locationPath NVARCHAR(MAX), updateDate DATETIME2, owner nvarchar(255), totalRows int, isFile bit);

    ;
WITH cte (directory_id, directory_name, parent_name, parent_id, is_team, updateDate, owner)
         AS (
        SELECT d.id,
               d.name,
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
WHERE d.id IN (SELECT id FROM @AllDirectories)

UNION ALL

SELECT c.directory_id,
       c.directory_name,
       d.name,
       d.parent_id,
       d.is_team,
       c.updateDate,
       c.owner
FROM directory_entity AS d WITH (NOLOCK), cte AS c
WHERE c.parent_id = d.id

    )

INSERT
INTO @result
SELECT directory_id   AS            id,
       directory_name AS            displayName,
       is_team        AS            isTeam,
       STRING_AGG(parent_name, '/') WITHIN GROUP (ORDER BY directory_id) AS locationPath, updateDate, owner,
            @totalRows AS totalRows, @isFile AS isFile
FROM cte
GROUP BY directory_id, directory_name, is_team, updateDate, owner

INSERT
INTO @result
SELECT d.id                                     as id,
       d.displayName                            as displayName,
       d.isTeam                                 as isTeam,
       ''                                       as locationPath,
       d.updateDate                             as updateDate,
       CONCAT(ac.first_name, ' ', ac.last_name) as owner,
       @totalRows                               AS totalRows,
       @isFile                                  AS isFile
FROM @AllDirectories d
         INNER JOIN account AS ac WITH (NOLOCK)
ON ac.id = d.accountId
WHERE d.parent_id = 0

SELECT id           AS id,
       displayName  AS displayName,
       isTeam       AS isTeam,
       locationPath AS locationPath,
       updateDate   as updateDate,
       owner        as owner,
       @totalRows   AS totalRows,
       @isFile      AS isFile
from @result


END