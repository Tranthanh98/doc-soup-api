
ALTER PROCEDURE [dbo].[sel_page_stats]
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
    select page as page, avgDuration/1000 as avgDuration, visit * 100/ (select sum(visit) from t) as percentVisit
    from t;

END
GO

