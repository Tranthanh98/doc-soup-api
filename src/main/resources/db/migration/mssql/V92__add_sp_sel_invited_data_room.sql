CREATE PROCEDURE [dbo].[sel_invited_data_room_by_user_id_and_company_id]
    @userId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER
AS
BEGIN

SELECT d.id as id, d.name as name, d.is_active as isActive, d.account_id as accountId, d.company_id as companyId, d.view_type as viewType,
       CONVERT(DATETIME2, d.created_date, 1) as createdDate,  CONVERT(DATETIME2, d.modified_date, 1) as modifiedDate, STRING_AGG(ISNULL(link.name, ' '), ',') as sharedWithAccount, (a.first_name + ' ' + a.last_name) as owner
from data_room d
INNER JOIN  data_room_user du
ON d.id=du.data_room_id
LEFT JOIN  account a
ON a.id=d.account_id
LEFT JOIN link
ON link.ref_id=d.id and link.document_id IS NULL
WHERE du.user_id=@userId and d.company_id=@companyId
GROUP BY d.id, d.name, a.first_name, a.last_name, d.id, d.is_active, d.account_id, d.company_id, d.view_type, d.created_by, d.modified_date, d.created_date

END
GO
