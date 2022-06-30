
ALTER PROCEDURE [dbo].[sel_contact_with_link_model_by_companyId_archived_is_false]
    @companyId UNIQUEIDENTIFIER
AS
BEGIN



CREATE TABLE #linkNameTable
(
    contactId varchar(255),
    linkNames NVARCHAR(max)
)

INSERT INTO #linkNameTable
SELECT c.id as contactId, STRING_AGG(CONVERT(NVARCHAR(max), lc.linkName), ',') as linkNames
FROM contact c WITH (NOLOCK)
left join (
    SELECT distinct c.id as contacId, l.id as linkId, la.name as linkName
    FROM contact c WITH (NOLOCK)
    INNER JOIN link_statistic s WITH (NOLOCK)
    ON c.id=s.contact_id
    LEFT JOIN link l WITH (NOLOCK)
    ON s.link_id = l.id
    LEFT JOIN link_accounts la
    ON l.link_accounts_id = la.id
    WHERE c.company_id=@companyId and c.archived=0
    GROUP BY c.id, l.id, la.name
    ) as lc
on c.id = lc.contacId
GROUP BY c.id


CREATE TABLE #linkCreatorTable
(
    contactId varchar(255),
    linkCreators NVARCHAR(max)
)

INSERT INTO #linkCreatorTable
select c.id as contactId, STRING_AGG(CONVERT(NVARCHAR(max), lc.linkCreator), ',') as linkCreators
FROM contact c WITH (NOLOCK)
left join (
select distinct c.id as contacId, a.id, CONCAT(a.first_name, ' ', a.last_name) as linkCreator
from contact c WITH (NOLOCK)
inner join link_statistic s WITH (NOLOCK)
on c.id=s.contact_id
left join link l WITH (NOLOCK)
on s.link_id = l.id
left join  account a WITH (NOLOCK)
on l.created_by=a.id
where c.company_id=@companyId and c.archived=0
GROUP BY c.id, a.id, a.first_name, a.last_name
    ) as lc
on c.id = lc.contacId
GROUP BY c.id

select c.id as contactId, c.email as contactName, c.archived as archived, CASE WHEN EXISTS (SELECT Id FROM link_statistic ls WHERE ls.contact_id = c.id and ls.signednda=1) THEN CONVERT(BIT, 1) ELSE CONVERT(BIT, 0)END AS signedNDA,
       sum(s.visit) as visits, CONVERT(DATETIME2, MAX(s.viewed_at), 1) as lastActivity, c.account_id as accountId, ln.linkNames as linkNames, lc.linkCreators as linkCreators
from  contact c WITH (NOLOCK)
INNER JOIN link_statistic s WITH (NOLOCK)
ON s.contact_id = c.id
    LEFT JOIN #linkNameTable ln
    ON ln.contactId = c.id
    LEFT JOIN #linkCreatorTable lc
    ON lc.contactId = c.id
where c.company_id=@companyId and c.archived=0
GROUP BY c.id ,c.email,c.archived,c.account_id, ln.linkNames, lc.linkCreators

    If(OBJECT_ID('tempdb..#linkNameTable') Is Not Null)
Begin
Drop Table #linkNameTable
End

If (OBJECT_ID('tempdb..#linkCreatorTable') Is Not Null)
Begin
Drop Table #linkCreatorTable
End

END
GO


ALTER PROCEDURE [dbo].[sel_contact_with_link_model_by_company_id]
    @companyId UNIQUEIDENTIFIER
AS
BEGIN

CREATE TABLE #linkNameTable
(
    contactId varchar(255),
    linkNames NVARCHAR(max)
)

INSERT INTO #linkNameTable
SELECT c.id as contactId, STRING_AGG(CONVERT(NVARCHAR(max), lc.linkName), ',') as linkNames
FROM contact c WITH (NOLOCK)
left join (
    SELECT distinct c.id as contacId, l.id as linkId, la.name as linkName
    FROM contact c WITH (NOLOCK)
    INNER JOIN link_statistic s WITH (NOLOCK)
    ON c.id=s.contact_id
    LEFT JOIN link l WITH (NOLOCK)
    ON s.link_id = l.id
    LEFT JOIN link_accounts la
    ON l.link_accounts_id = la.id
    WHERE c.company_id=@companyId
    GROUP BY c.id, l.id, la.name
) as lc
on c.id = lc.contacId
GROUP BY c.id


CREATE TABLE #linkCreatorTable
(
    contactId varchar(255),
    linkCreators NVARCHAR(max)
)

    INSERT INTO #linkCreatorTable
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

select c.id as contactId, c.email as contactName, c.archived as archived, CASE WHEN EXISTS (SELECT Id FROM link_statistic ls WHERE ls.contact_id = c.id and ls.signednda=1) THEN CONVERT(BIT, 1) ELSE CONVERT(BIT, 0) END AS signedNDA,
       sum(s.visit) as visits, CONVERT(DATETIME2, MAX(s.viewed_at), 1) as lastActivity, c.account_id as accountId, ln.linkNames as linkNames, lc.linkCreators as linkCreators
from  contact c
          INNER JOIN link_statistic s WITH (NOLOCK)
ON s.contact_id = c.id
    LEFT JOIN #linkNameTable ln
    ON ln.contactId = c.id
    LEFT JOIN #linkCreatorTable lc
    ON lc.contactId = c.id
where c.company_id=@companyId
GROUP BY c.id ,c.email,c.archived,c.account_id, ln.linkNames, lc.linkCreators

    If(OBJECT_ID('tempdb..#linkNameTable') Is Not Null)
Begin
Drop Table #linkNameTable
End

If (OBJECT_ID('tempdb..#linkCreatorTable') Is Not Null)
Begin
Drop Table #linkCreatorTable
End

END
GO