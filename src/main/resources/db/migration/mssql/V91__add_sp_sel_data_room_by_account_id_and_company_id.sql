CREATE PROCEDURE [dbo].[sel_data_room_by_account_id_and_company_id]
    @accountId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER
AS
BEGIN

Select d.id as id, d.name as name, d.is_active as isActive, d.account_id as accountId, d.company_id as companyId, d.view_type as viewType,
CONVERT(DATETIME2, d.created_date, 1) as createdDate, CONVERT(DATETIME2, d.modified_date, 1), STRING_AGG(ISNULL(link.name, ' '), ',') as sharedWithAccount, (a.first_name + ' ' + a.last_name) as owner
from data_room d
left JOIN  account a
on a.id=d.account_id
LEFT JOIN link
ON link.ref_id=d.id and link.document_id IS NULL
WHERE d.account_id=@accountId and d.company_id=@companyId
GROUP BY d.id, d.name, a.first_name, a.last_name, d.id, d.is_active, d.account_id, d.company_id, d.view_type, d.created_by, d.modified_date, d.created_date

END
GO
