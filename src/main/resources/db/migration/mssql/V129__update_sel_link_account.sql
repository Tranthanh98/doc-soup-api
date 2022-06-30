GO
ALTER PROCEDURE [dbo].[sel_link_account]
    @status NVARCHAR(10) NULL,
    @mode NVARCHAR(10) NULL,
    @archived BIT NULL,
    @companyId NVARCHAR(36),
    @accountId NVARCHAR(36)
AS
BEGIN

DECLARE @linkAccountWithAllVisits TABLE (
    linkAccountId BIGINT,
    totalVisitor BIGINT
)

INSERT INTO @linkAccountWithAllVisits
SELECT a.linkAccountId, COUNT(a.contactId)
FROM (
         SELECT DISTINCT la.id as linkAccountId, s.contact_id as contactId
         FROM link_statistic s WITH (NOLOCK)
    INNER JOIN link l WITH (NOLOCK)
         ON s.link_id = l.id
             INNER JOIN link_accounts la WITH (NOLOCK)
         ON la.id = l.link_accounts_id
         WHERE s.contact_id IS NOT NULL AND la.company_id = @companyId
     ) as a
GROUP BY a.linkAccountId

    INSERT INTO @linkAccountWithAllVisits
SELECT a.linkAccountId, COUNT(a.device_id)
FROM (
         SELECT DISTINCT la.id as linkAccountId, s.device_id as device_id
         FROM link_statistic s WITH (NOLOCK)
    INNER JOIN link l WITH (NOLOCK)
         ON s.link_id = l.id
             INNER JOIN link_accounts la  WITH (NOLOCK)
         ON la.id = l.link_accounts_id
         WHERE s.contact_id IS NULL AND la.company_id = @companyId
     ) as a
GROUP BY a.linkAccountId

DECLARE @linkAccountWithTotalVisit TABLE (
    linkAccountId BIGINT,
    totalVisitor BIGINT
)

INSERT INTO @linkAccountWithTotalVisit
SELECT a.linkAccountId, SUM(a.totalVisitor)
FROM @linkAccountWithAllVisits a
GROUP BY a.linkAccountId

DECLARE @linkCreatorTable TABLE
(
    linkAccountId BIGINT,
    contributors NVARCHAR(max)
)

INSERT INTO @linkCreatorTable
select la.id as linkAccountId, STRING_AGG(CONVERT(NVARCHAR(max), lc.linkCreator), ',') as linkCreators
FROM link_accounts la WITH (NOLOCK)
inner join (
select distinct la.id as linkAccountId, a.id, CONCAT(a.first_name, ' ', a.last_name) as linkCreator
from link_accounts la WITH (NOLOCK)
inner join link l WITH (NOLOCK)
on la.id = l.link_accounts_id
left join  account a WITH (NOLOCK)
on l.created_by=a.id
where la.company_id=@companyId
GROUP BY la.id, a.id, a.first_name, a.last_name
    ) as lc
on la.id = lc.linkAccountId
GROUP BY la.id

DECLARE @archivedResult TABLE(
    id BIGINT,
    name NVARCHAR(255),
    created_by NVARCHAR(36),
    contributors NVARCHAR(max),
    lastActivity DATETIME2,
    totalVisit BIGINT,
    totalVisitor BIGINT,
    totalDuration BIGINT,
    archived BIT
)

IF @archived IS NULL
    INSERT INTO @archivedResult
SELECT la.id, la.name, la.created_by, lc.contributors as contributors, CONVERT(DATETIME2, ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.created_date), ISNULL(MAX(la.modified_date), MAX(la.created_date)))), 1) as lastActivity, (CASE WHEN SUM(s.visit) > 0 THEN SUM(s.visit) ELSE 0 END) as totalVisit, lav.totalVisitor as totalVisitor, SUM(s.duration) as totalDuration, la.archived as archived
FROM link_accounts la WITH (NOLOCK)
    LEFT JOIN link l WITH (NOLOCK)
ON l.link_accounts_id = la.id
    LEFT JOIN link_statistic s WITH (NOLOCK)
ON s.link_id = l.id
    LEFT JOIN @linkAccountWithTotalVisit lav
    ON lav.linkAccountId = la.id
    LEFT JOIN @linkCreatorTable lc
    ON lc.linkAccountId = la.id
WHERE la.company_id = @companyId
GROUP BY la.name, la.id, la.created_by, lc.contributors, lav.totalVisitor, la.archived
    ELSE
INSERT INTO @archivedResult
SELECT la.id, la.name, la.created_by, lc.contributors as contributors, CONVERT(DATETIME2, ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.created_date), ISNULL(MAX(la.modified_date), MAX(la.created_date)))), 1) as lastActivity, (CASE WHEN SUM(s.visit) > 0 THEN SUM(s.visit) ELSE 0 END) as totalVisit, lav.totalVisitor as totalVisitor, SUM(s.duration) as totalDuration, la.archived as archived
FROM link_accounts la WITH (NOLOCK)
    LEFT JOIN link l WITH (NOLOCK)
ON l.link_accounts_id = la.id
    LEFT JOIN link_statistic s WITH (NOLOCK)
ON s.link_id = l.id
    LEFT JOIN @linkAccountWithTotalVisit lav
    ON lav.linkAccountId = la.id
    LEFT JOIN @linkCreatorTable lc
    ON lc.linkAccountId = la.id
WHERE la.company_id = @companyId and la.archived = @archived
GROUP BY la.name, la.id, la.created_by, lc.contributors, lav.totalVisitor, la.archived

DECLARE @statusResult TABLE(
    id BIGINT,
    name NVARCHAR(255),
    created_by NVARCHAR(36),
    contributors NVARCHAR(max),
    lastActivity DATETIME2,
    totalVisit BIGINT,
    totalVisitor BIGINT,
    totalDuration BIGINT,
    archived BIT
)

IF @status = 'active'
    INSERT INTO @statusResult
SELECT ar.*
FROM @archivedResult ar
WHERE ar.totalVisit > 0
    ELSE IF @status = 'idle'
INSERT INTO @statusResult
SELECT ar.*
FROM @archivedResult ar
WHERE ar.totalVisit = 0
    ELSE IF @status IS NULL OR @status = ''
INSERT INTO @statusResult
SELECT ar.*
FROM @archivedResult ar

DECLARE @result TABLE(
    id BIGINT,
    name NVARCHAR(255),
    contributors NVARCHAR(max),
    lastActivity DATETIME2,
    totalVisit BIGINT,
    totalVisitor BIGINT,
    totalDuration BIGINT,
    archived BIT
)

IF @mode = 'personal'
    INSERT INTO @result
SELECT sr.id, sr.name, sr.contributors, sr.lastActivity, sr.totalVisit, sr.totalVisitor, sr.totalDuration, sr.archived
FROM @statusResult sr
WHERE sr.created_by = @accountId
    ELSE IF @mode = 'all'
INSERT INTO @result
SELECT sr.id, sr.name, sr.contributors, sr.lastActivity, sr.totalVisit, sr.totalVisitor, sr.totalDuration, sr.archived
FROM @statusResult sr

SELECT r.id id, r.name as name, r.contributors as contributors, r.lastActivity as lastActivity, r.totalVisit as totalVisit, r.totalVisitor as totalVisitor, r.totalDuration as totalDuration, r.archived as archived
FROM @result r

END
GO
