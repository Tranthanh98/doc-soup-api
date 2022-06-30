ALTER PROCEDURE [dbo].[sel_invited_data_room_by_user_id_and_company_id]
    @userId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER
AS
BEGIN

SELECT d.id as id, d.name as name, d.is_active as isActive, d.account_id as accountId, d.company_id as companyId, d.view_type as viewType,
       CONVERT(DATETIME2, d.created_date, 1) as createdDate, STRING_AGG(ISNULL(la.name, ' '), ',') as sharedWithAccount, (a.first_name + ' ' + a.last_name) as owner,
       CASE WHEN EXISTS (SELECT id FROM link l WITH (NOLOCK) WHERE l.ref_id = d.id and l.document_id IS NULL and l.status=0) THEN CONVERT(BIT, 0) ELSE CONVERT(BIT, 1) END AS disabledAllLink
    from data_room d WITH (NOLOCK)
    LEFT JOIN  account a WITH (NOLOCK)
    ON a.id=d.account_id
    LEFT JOIN link WITH (NOLOCK)
    ON link.ref_id=d.id and link.document_id IS NULL
    LEFT JOIN link_accounts la
    ON la.id = link.link_accounts_id
    WHERE d.company_id=@companyId
    GROUP BY d.id, d.name, a.first_name, a.last_name, d.is_active, d.account_id, d.company_id, d.view_type, d.created_by, d.created_date

END
GO