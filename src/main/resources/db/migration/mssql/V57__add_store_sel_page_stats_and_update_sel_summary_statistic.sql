
CREATE PROCEDURE [dbo].[sel_page_stats]
@fileId  bigint = 0
AS
BEGIN
    SET NOCOUNT ON;
    with t(page, avgDuration,visit)
             as
             (
                 select p.page as page,avg(p.duration) as avgDuration,SUM(p.visit)  from page_statistic p
                                                                                             INNER join link l
                                                                                                        on p.link_id = l.id
                 where l.ref_id = @fileId and document_id is not null
                 group by p.page
             )
    select page, avgDuration, visit * 100/ (select sum(visit) from t)
    from t;

END
GO

ALTER PROCEDURE [dbo].[sel_summary_statistic_on_file]
@fileId  bigint = 0
AS
BEGIN

    SET NOCOUNT ON
    declare @visit bigint =0;
    declare @avgViewed FLOAT =0;

    select  @visit = sum(s.visit)  from link_statistic s
                                            INNER JOIN link l
                                                       on s.link_id = l.id
    WHERE l.ref_id=@fileId and l.document_id is not null;

    with t(pageNum,visit)
             as
             (
                 select p.page ,SUM(p.visit)  from page_statistic p
                                                       INNER join link l
                                                                  on p.link_id = l.id
                 where l.ref_id = @fileId and document_id is not null
                 group by p.page
             )
    select @avgViewed= (select count(pageNum) from t where visit >0)*100 / count(pageNum)
    from t;

    select top 1 1 AS AID, p.page as topPage, SUM(p.visit) as topPageVisits, SUM(p.duration) as topPageDuration,@visit as visits,@avgViewed as avgViewed
    from page_statistic p
             INNER JOIN link l on p.link_id = l.id
    WHERE l.ref_id= @fileId and l.document_id is not null
    GROUP BY p.page
    order by AVG(p.duration) desc

END
GO