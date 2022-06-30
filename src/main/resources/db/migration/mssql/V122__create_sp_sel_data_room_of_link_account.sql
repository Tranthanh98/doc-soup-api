GO
CREATE PROCEDURE [dbo].[sel_data_room_of_link_account]
    @linkAccountId BIGINT
AS
BEGIN

    SELECT d.id as id, d.name as name,(select string_agg(value,', ') from (select distinct value from string_split(string_agg(la.name, ','),',')) t) as sharedWithAccount,
       CONVERT(DATETIME2, d.created_date, 1) as createdDate, CONVERT(DATETIME2, d.modified_date, 1) as modifiedDate, (a.first_name + ' ' + a.last_name) as owner, d.account_id as accountId, d.company_id as companyId
    FROM (
        select d.id, d.name, d.created_date,d.created_by, d.modified_date, d.account_id, d.company_id
        from data_room d
        inner join link l on d.id=l.ref_id
        WHERE l.document_id is NULL and l.link_accounts_id=@linkAccountId
        group by d.id, d.name, d.created_date,d.created_by, d.modified_date, d.account_id, d.company_id
     ) as d
    left JOIN  account a WITH (NOLOCK)
    on a.id=d.account_id
    left join link l on l.ref_id = d.id and l.document_id is NULL
    left JOIN link_accounts la on la.id = l.link_accounts_id
    GROUP BY d.id, d.name, d.created_date,d.created_by, d.modified_date, a.first_name, a.last_name, d.account_id, d.company_id
    ORDER BY d.modified_date, d.created_date DESC

END
GO
