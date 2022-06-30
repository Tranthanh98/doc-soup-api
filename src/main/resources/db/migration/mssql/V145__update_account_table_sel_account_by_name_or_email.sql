ALTER TABLE account
    ADD enable bit NOT NULL default 1
    GO

CREATE PROCEDURE [dbo].[sel_search_account_by_name_or_email]
    @keyword VARCHAR(255) ,
    @page INTEGER,
    @pageSize INTEGER
AS
BEGIN

    DECLARE   @accoutnUser TABLE ( id UNIQUEIDENTIFIER )
    INSERT into @accoutnUser
    SELECT id FROM account  WITH (NOLOCK)
    WHERE first_name LIKE CONCAT('%', @keyword, '%') OR email LIKE CONCAT('%', @keyword, '%')
    ORDER BY first_name DESC OFFSET (@page * @pageSize) ROWS FETCH NEXT @pageSize ROWS ONLY


    DECLARE @totalRows INT = (SELECT COUNT(*) AS COUNT FROM account WITH (NOLOCK)
            WHERE first_name LIKE CONCAT('%', @keyword, '%') OR email LIKE CONCAT('%', @keyword, '%'))


    SELECT ac.id AS id, CONCAT(ac.first_name, ' ', ac.last_name) AS fullName, ac.email AS email,
           CONVERT(DATETIME2, ISNULL(ac.check_in_time, ac.created_date), 1) AS lastActive, ac.active_company_id AS activeCompanyId,
           ac.phone AS phone, CONVERT(DATETIME2, ac.created_date, 1) AS registerDate, SUM(fe.size) AS usedSpace, ac.enable AS enable, @totalRows as totalRows
    FROM @accoutnUser temp
             LEFT JOIN account ac ON temp.id = ac.id
             LEFT JOIN file_entity fe ON fe.account_id = ac.id
    GROUP BY ac.id, ac.first_name, ac.last_name, ac.email, ac.check_in_time, ac.active_company_id, ac.phone, ac.created_date, ac.enable

END
GO