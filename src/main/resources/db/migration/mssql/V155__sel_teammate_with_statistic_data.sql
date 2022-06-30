CREATE PROCEDURE [dbo].[sel_teammate_with_statistic_data]
    @numOfDay INTEGER ,
    @companyId UNIQUEIDENTIFIER,
    @page INTEGER ,
    @pageSize INTEGER
AS
BEGIN
    DECLARE @temp TABLE(
        account_id UNIQUEIDENTIFIER,
        member_type INTEGER,
        status INTEGER,
        role varchar(50),
        name nVARCHAR(100),
        email VARCHAR(100),
        links INTEGER,
        dataRooms INTEGER,
        roomLinks INTEGER,
        visits bigint
    )

    INSERT into @temp SELECT cu.account_id, cu.member_type, cu.status, cu.role, CONCAT(a.first_name, ' ', a.last_name) as name , cu.email,

            (SELECT count(*) from link cl WHERE cl.created_by= cu.account_id and cl.company_id = @companyId and cl.document_id is NOT NULL
                        and cl.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE() ) as links,

            (SELECT count(*) from data_room dr WHERE dr.created_by= cu.account_id and dr.company_id = @companyId
                    and dr.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE() ) as dataRooms,

            (SELECT count(*) from link rl WHERE rl.created_by= cu.account_id and rl.company_id = @companyId and rl.document_id is null
                    and rl.created_date BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE()) as roomLinks,

            (select sum(ls.visit) from link_statistic ls WHERE ls.link_id =l.id  and ls.viewed_at BETWEEN DateAdd(DD,-@numOfDay,GETDATE() ) and GETDATE() GROUP BY ls.link_id) as visits

            from company_user cu WITH (NOLOCK)
            right JOIN account a  on a.id = cu.account_id
            LEFT JOIN link l  on l.created_by = a.id
            WHERE cu.company_id = @companyId AND cu.status = 1
            GROUP BY cu.account_id, cu.member_type, cu.status, cu.role, a.first_name,  a.last_name, cu.email, l.id

    DECLARE @resulTable TABLE(
        account_id UNIQUEIDENTIFIER,
        member_type INTEGER,
        status INTEGER,
        role varchar(50),
        name nVARCHAR(100),
        email VARCHAR(100),
        links INTEGER,
        dataRooms INTEGER,
        roomLinks INTEGER,
        visits bigint
    )

    INSERT into @resulTable SELECT  account_id, member_type, status, role, name, email, links, dataRooms, roomLinks , sum(visits) as visits
                            FROM @temp GROUP BY account_id, member_type ,status ,role ,name ,email ,links ,dataRooms,roomLinks

    DECLARE @totalRows INT = (SELECT COUNT(*) AS totalRows FROM @resulTable )

    select account_id AS accountId, member_type AS memberType,status ,role ,name AS fullName,email ,links , dataRooms,roomLinks,  ISNULL(visits,0) as visits , @totalRows as totalRows
    from @resulTable
    ORDER BY name DESC OFFSET (@page * @pageSize) ROWS FETCH NEXT @pageSize ROWS ONLY
END
GO
