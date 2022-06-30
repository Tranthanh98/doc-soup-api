
UPDATE feature_flag SET feature_key = 'IncludeStorage' WHERE unit = 'GB/user'

GO
ALTER PROCEDURE [dbo].[sel_summary_dashboard]
AS
BEGIN
    declare @temp table (
        category nvarchar(255),
        total bigint,
        newValue bigint
    );

insert into @temp
select N'users' as category,
       COUNT(a1.id) as total,
       (select m.newUser
        from (
                 select count(a.id) as newUser from account AS a WITH(NOLOCK)
                 where a.created_date >= cast( GETUTCDATE() as date ))
                 as m)
           as newValue
from account as a1 WITH(NOLOCK)

insert into @temp
select N'links' as category,
       COUNT(l1.id) as total,
       (select m.newLink
        from (
                 select count(a.id) as newLink from link AS a WITH(NOLOCK)
                 where a.created_date >= cast( GETUTCDATE() as date ))
                 as m)
           as newValue
from link as l1 WITH(NOLOCK)

insert into @temp
select N'docs' as category,
       COUNT(d1.id) as total,
       (select m.newDocs
        from (
                 select count(a.id) as newDocs from file_entity AS a WITH(NOLOCK)
                 where a.created_date >= cast( GETUTCDATE() as date ))
                 as m)
           as newValue
from file_entity as d1 WITH(NOLOCK)

insert into @temp
select N'monthlyEarnings' as category,
       sum(d1.total_amount) as total,
       (select m.newEarning
        from (
                 select sum(a.total_amount) as newEarning from payment_history AS a WITH(NOLOCK)
                 where a.status = 'PAYMENT_PAID'
                   and a.created_date BETWEEN  DATEADD(month, DATEDIFF(month, 0, cast(GETUTCDATE() as date)), 0) and cast (GETUTCDATE() as date))
                 as m)
           as newValue
from payment_history as d1 WITH(NOLOCK)
where d1.created_date BETWEEN DATEADD(month, DATEDIFF(month, 0, cast(DATEADD(mm, -1, GETUTCDATE()) as date)), 0) AND cast(DATEADD(mm, -1, GETUTCDATE()) as date)
  and d1.status = 'PAYMENT_PAID'

select category as category, total as total, newValue as newValue
from @temp
END
GO
