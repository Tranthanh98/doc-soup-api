GO
CREATE PROCEDURE [dbo].[sel_summary_dashboard]
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
                     select sum(a.total_amount) as newEarning from billing_info AS a WITH(NOLOCK)
                     where a.process_status = 1
                       and a.status = 2
                       and a.created_date BETWEEN  DATEADD(month, DATEDIFF(month, 0, cast(GETUTCDATE() as date)), 0) and cast (GETUTCDATE() as date))
                     as m)
               as newValue
    from billing_info as d1 WITH(NOLOCK)
    where created_date BETWEEN DATEADD(month, DATEDIFF(month, 0, cast(DATEADD(mm, -1, GETUTCDATE()) as date)), 0) AND cast(DATEADD(mm, -1, GETUTCDATE()) as date)
      and d1.[status] = 2 and d1.process_status = 1

    select category as category, total as total, newValue as newValue
    from @temp
END
GO

--- get links and documents

CREATE PROCEDURE [dbo].[sel_documents_and_links]
    @groupBy VARCHAR(10),
    @startDate DATETIME = NULL,
    @endDate DATETIME = NULL,
    @errorCode INT = 0 OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SET @errorCode = 0;

    if @startDate is null and @endDate is null
    BEGIN
        SET @errorCode = -999;
        RETURN;
    END

    declare @temp table (
        created_date date,
        documents int,
        links int
    );

    insert into @temp
    select cast(created_date as date) as created_date, count(id) as documents, 0 as links from document with(nolock)
    where (@startDate is not null and @endDate is not null and created_date BETWEEN @startDate and @endDate)
       or (@startDate is null and created_date < @endDate)
       or (@endDate is null and created_date > @startDate)
    group by cast(created_date as date);

    insert into @temp
    select cast(created_date as date)as created_date, 0 as documents, count(id) as links from link with(nolock)
    where (@startDate is not null and @endDate is not null and created_date BETWEEN @startDate and @endDate)
       or (@startDate is null and created_date < @endDate)
       or (@endDate is null and created_date > @startDate)
    group by cast(created_date as date);

    if @groupBy = 'DAILY'
        begin
            select convert (varchar(20), created_date) as createdDate, sum(documents) as documents, sum(links) as links from @temp
            group by created_date
        end
    else if @groupBy = 'WEEKLY'
        begin
            select convert (varchar(20), DATEPART(ww, created_date)) as createdDate, sum(documents) as documents, sum(links) as links from @temp
            group by DATEPART(ww, created_date)
        end
    else
        select CONCAT(year(created_date) , '-', MONTH(created_date)) as createdDate, sum(documents) as documents, sum(links) as links from @temp
        group by CONCAT(year(created_date) , '-', MONTH(created_date))
END
GO


--- get activity

CREATE PROCEDURE [dbo].[sel_activities]
    @groupBy VARCHAR(10),
    @startDate DATETIME = NULL,
    @endDate DATETIME = NULL,
    @errorCode INT = 0 OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    SET @errorCode = 0;

    if @startDate is null and @endDate is null
    BEGIN
            SET @errorCode = -999;
            RETURN;
    END

    declare @temp table (
        created_date date,
        links int,
        visits int
    );

    insert into @temp
    select cast(created_date as date) as created_date, count(id) as links, 0 as visits from link with(nolock)
    where (@startDate is not null and @endDate is not null and created_date BETWEEN @startDate and @endDate)
       or (@startDate is null and created_date < @endDate)
       or (@endDate is null and created_date > @startDate)
    group by cast(created_date as date);

    insert into @temp
    select cast(l.created_date as date)as created_date, 0 as links, sum(la.visit) as visits from link_statistic as la with(nolock)
    join link as l on l.id = la.link_id
    where (@startDate is not null and @endDate is not null and created_date BETWEEN @startDate and @endDate)
       or (@startDate is null and created_date < @endDate)
       or (@endDate is null and created_date > @startDate)
    group by  cast(l.created_date as date);

    if @groupBy = 'DAILY'
        begin
            select convert (varchar(20), created_date) as createdDate, sum(links) as links, sum(visits) as visits from @temp
            group by created_date
        end
    else if @groupBy = 'WEEKLY'
        begin
            select convert (varchar(20), DATEPART(ww, created_date)) as createdDate, sum(links) as links, sum(visits) as visits from @temp
            group by DATEPART(ww, created_date)
        end
    else
        select CONCAT(year(created_date) , '-', MONTH(created_date)) as createdDate, sum(links) as links, sum(visits) as visits from @temp
        group by CONCAT(year(created_date) , '-', MONTH(created_date))

END
GO