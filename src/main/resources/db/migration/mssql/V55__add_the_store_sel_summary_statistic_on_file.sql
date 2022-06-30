CREATE PROCEDURE [dbo].[sel_summary_statistic_on_file]
@fileId  bigint = 0
AS
BEGIN

    SET NOCOUNT ON
    SELECT A.topPage,A.topPageVisits,A.topPageDuration,B.visits,C.numOfViewedPage,D.totalPage FROM
        (
            select top 1 1 AS AID, p.page as topPage, COUNT(p.id) as topPageVisits, SUM(p.duration) as topPageDuration
            from page_statistic p
                     INNER JOIN link_statistic s
                                on p.link_statistic_id = s.id
                     INNER JOIN link l
                                on p.link_id = l.id
            WHERE l.ref_id= @fileId and l.document_id is not null
            GROUP BY p.page
            order by AVG(p.duration) desc
        ) A
            LEFT JOIN
        (
            select 1 AS BID, COUNT(s.id) as visits from link_statistic s
                                                            INNER JOIN link l
                                                                       on s.link_id = l.id
            WHERE l.ref_id=@fileId and l.document_id is not null

        ) B
        ON A.AID = B.BID

            LEFT JOIN
        (select 1 AS CID, count(p.id) as numOfViewedPage from page_statistic p
                                                                  INNER JOIN link l
                                                                             on p.link_id = l.id
         WHERE l.ref_id= @fileId and l.document_id is not null and p.duration > 0
        ) C
        ON A.AID = C.CID

            LEFT JOIN
        (
            select 1 AS DID, count(p.id) as totalPage from page_statistic p
                                                               INNER JOIN link l
                                                                          on p.link_id = l.id
            WHERE l.ref_id= @fileId and l.document_id is not null
        ) D
        ON C.CID = D.DID

END
GO