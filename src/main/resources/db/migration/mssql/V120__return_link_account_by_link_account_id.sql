GO
CREATE PROCEDURE [dbo].[sel_link_account_by_id]
    @linkAccountId bigint
AS
BEGIN

    select la.id as id,
           la.name as name,
           la.archived as archived,
           CONVERT(DATETIME2, ISNULL(MAX(ls.viewed_at), ISNULL(MAX(l.created_date), ISNULL(MAX(la.modified_by), MAX(la.created_date)))), 1) as lastActivity,
           SUM(ls.visit) as totalVisit,
           SUM(ls.duration) as totalDuration
    from link_accounts as la WITH(NOLOCK)
             left join link as l WITH(NOLOCK) on la.id = l.link_accounts_id
             left join link_statistic as ls WITH(NOLOCK) on ls.link_id = l.id
    where la.id = @linkAccountId
    group by la.id, la.name, la.archived, la.created_date, la.modified_date
END
GO
