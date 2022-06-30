
ALTER PROCEDURE [dbo].[sel_contact]
    @companyId NVARCHAR(36),
    @accountId NVARCHAR(36),
    @mode NVARCHAR(10) NULL,
    @archived BIT NULL
AS
BEGIN

DECLARE  @linkNameTable TABLE
(
    contactId varchar(255),
    linkNames NVARCHAR(max)
)

INSERT INTO @linkNameTable
SELECT c.id as contactId,  CONCAT('[', STRING_AGG(lc.linkName, ','), ']') as linkNames
FROM contact c WITH (NOLOCK)
left join (
    SELECT distinct c.id as contacId, CONCAT('{ "id":', la.id, ', "name": "', la.name, '"}') as linkName
    FROM contact c WITH (NOLOCK)
    INNER JOIN link_statistic s WITH (NOLOCK)
    ON c.id=s.contact_id
    LEFT JOIN link l WITH (NOLOCK)
    ON s.link_id = l.id
    LEFT JOIN link_accounts la
    ON l.link_accounts_id = la.id
    WHERE c.company_id=@companyId
    GROUP BY c.id, la.name, la.id
) as lc
on c.id = lc.contacId
GROUP BY c.id


DECLARE  @linkCreatorTable TABLE
(
    contactId varchar(255),
    linkCreators NVARCHAR(max)
)

INSERT INTO @linkCreatorTable
select c.id as contactId, STRING_AGG(CONVERT(NVARCHAR(max), lc.linkCreator), ',') as linkCreators
FROM contact c
         left join (
    select distinct c.id as contacId, a.id, CONCAT(a.first_name, ' ', a.last_name) as linkCreator
    from contact c WITH (NOLOCK)
    inner join link_statistic s WITH (NOLOCK)
    on c.id=s.contact_id
        left join link l WITH (NOLOCK)
    on s.link_id = l.id
        left JOIN  account a WITH (NOLOCK)
    on l.created_by=a.id
    where c.company_id=@companyId
    GROUP BY c.id, a.id, a.first_name, a.last_name
) as lc
                   on c.id = lc.contacId
GROUP BY c.id

DECLARE @archivedResult TABLE
(
    contactId BIGINT,
    contactName NVARCHAR(255),
    archived BIT,
    signedNDA BIT,
    visits INT,
    lastActivity DATETIME2,
    accountId NVARCHAR(36),
    linkNames NVARCHAR(MAX),
    linkCreators NVARCHAR(MAX),
    createdBy NVARCHAR(36)
)

IF @archived IS NULL
    INSERT INTO @archivedResult
select c.id as contactId, c.email as contactName, c.archived as archived, CASE WHEN EXISTS (SELECT Id FROM link_statistic ls WHERE ls.contact_id = c.id and ls.signednda=1) THEN CONVERT(BIT, 1) ELSE CONVERT(BIT, 0) END AS signedNDA,
       sum(s.visit) as visits, CONVERT(DATETIME2, MAX(s.viewed_at), 1) as lastActivity, c.account_id as accountId, ln.linkNames as linkNames, lc.linkCreators as linkCreators, c.created_by as createdBy
from  contact c
          INNER JOIN link_statistic s WITH (NOLOCK)
ON s.contact_id = c.id
    LEFT JOIN @linkNameTable ln
    ON ln.contactId = c.id
    LEFT JOIN @linkCreatorTable lc
    ON lc.contactId = c.id
where c.company_id=@companyId
GROUP BY c.id ,c.email,c.archived,c.account_id, ln.linkNames, lc.linkCreators, c.created_by
    ELSE
INSERT INTO @archivedResult
select c.id as contactId, c.email as contactName, c.archived as archived, CASE WHEN EXISTS (SELECT Id FROM link_statistic ls WHERE ls.contact_id = c.id and ls.signednda=1) THEN CONVERT(BIT, 1) ELSE CONVERT(BIT, 0) END AS signedNDA,
       sum(s.visit) as visits, CONVERT(DATETIME2, MAX(s.viewed_at), 1) as lastActivity, c.account_id as accountId, ln.linkNames as linkNames, lc.linkCreators as linkCreators, c.created_by as createdBy
from  contact c
          INNER JOIN link_statistic s WITH (NOLOCK)
ON s.contact_id = c.id
    LEFT JOIN @linkNameTable ln
    ON ln.contactId = c.id
    LEFT JOIN @linkCreatorTable lc
    ON lc.contactId = c.id
where c.company_id=@companyId and c.archived=@archived
GROUP BY c.id ,c.email,c.archived,c.account_id, ln.linkNames, lc.linkCreators, c.created_by

DECLARE @result TABLE
(
    contactId BIGINT,
    archived BIT,
    contactName NVARCHAR(255),
    signedNDA BIT,
    visits INT,
    lastActivity DATETIME2,
    accountId NVARCHAR(36),
    linkNames NVARCHAR(MAX),
    linkCreators NVARCHAR(MAX)
)

IF @mode = 'personal'
    INSERT INTO @result
SELECT ar.contactId, ar.archived, ar.contactName, ar.signedNDA, ar.visits, ar.lastActivity, ar.accountId, ar.linkNames, ar.linkCreators
FROM @archivedResult ar
WHERE ar.createdBy = @accountId
    ELSE IF @mode = 'all'
INSERT INTO @result
SELECT ar.contactId, ar.archived, ar.contactName, ar.signedNDA, ar.visits, ar.lastActivity, ar.accountId, ar.linkNames, ar.linkCreators
FROM @archivedResult ar

SELECT r.contactId as contactId, r.archived as archived, r.contactName as contactName, r.signedNDA as signedNDA, r.visits as visits, r.lastActivity as lastActivity, r.accountId as accountId, r.linkNames as linkNames, r.linkCreators as linkCreators
FROM @result r

END
GO
