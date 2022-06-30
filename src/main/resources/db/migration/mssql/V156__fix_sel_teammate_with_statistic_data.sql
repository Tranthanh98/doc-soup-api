ALTER PROCEDURE [dbo].[sel_teammate_with_statistic_data]
    @numOfDay INTEGER ,
    @companyId UNIQUEIDENTIFIER,
    @page INTEGER ,
    @pageSize INTEGER
AS
BEGIN
    DECLARE @totalRows int = (SELECT count(*) from (
                        SELECT cu.account_id, cu.member_type, cu.status, cu.role, CONCAT(a.first_name, ' ', a.last_name) as name , cu.email
                        from company_user cu WITH (NOLOCK)
                        right JOIN account a  on a.id = cu.account_id
                        LEFT JOIN link l  on l.created_by = a.id
                        WHERE cu.company_id = @companyId and cu.[status] =1
                        GROUP BY cu.account_id, cu.member_type, cu.status, cu.role, a.first_name,  a.last_name, cu.email
                     )src)

    DECLARE @visits TABLE (
        created_by  UNIQUEIDENTIFIER,
        visits bigint
    )

    INSERT into @visits select l.created_by, sum(ls.visit) from link l
        LEFT JOIN link_statistic ls on l.id = ls.link_id
        WHERE l.company_id = @companyId
        and ls.viewed_at BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE()
        GROUP BY l.created_by;

    WITH temp (accountId ,    memberType ,    status ,    role ,    fullName ,    email ,    links ,    dataRooms,    roomLinks )
        as (
            SELECT cu.account_id, cu.member_type, cu.status, cu.role, CONCAT(a.first_name, ' ', a.last_name) , cu.email,

                   (SELECT count(*) from link cl WHERE cl.created_by= cu.account_id and cl.company_id = @companyId and cl.document_id is NOT NULL
                        and cl.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE() ) as links,

                   (SELECT count(*) from data_room dr WHERE dr.created_by= cu.account_id and dr.company_id = @companyId
                        and dr.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE() ) as dataRooms,

                   (SELECT count(*) from link rl WHERE rl.created_by= cu.account_id and rl.company_id = @companyId and rl.document_id is null
                        and rl.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE()) as roomLinks

            from company_user cu WITH (NOLOCK)
            right JOIN account a  on a.id = cu.account_id
            LEFT JOIN link l  on l.created_by = a.id
            WHERE cu.company_id = @companyId and cu.[status] =1
            GROUP BY cu.account_id, cu.member_type, cu.status, cu.role, a.first_name,  a.last_name, cu.email, l.id
        )

    SELECT tp.*, ISNULL( v.visits,0) as visits, @totalRows as totalRows from temp tp
    LEFT JOIN @visits v ON v.created_by = tp.accountId
    GROUP BY accountId, memberType ,    status ,    role ,    fullName ,    email ,    links ,    dataRooms,    roomLinks , v.visits
    ORDER BY tp.fullName DESC OFFSET (@page * @pageSize) ROWS FETCH NEXT @pageSize ROWS ONLY

END
GO
