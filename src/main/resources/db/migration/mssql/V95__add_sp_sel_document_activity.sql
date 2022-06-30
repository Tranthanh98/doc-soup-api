
CREATE PROCEDURE [dbo].[sel_document_activity]
    @userId NVARCHAR(36),
    @companyId UNIQUEIDENTIFIER,
    @top int,
    @sortDirection VARCHAR(4),
    @dateRecent int
AS
BEGIN

    CREATE TABLE #fileWithViews
    (
        fileId bigint,
        views NVARCHAR(max)
    )

    INSERT INTO #fileWithViews
    select f.id,  SUM(s.visit)
    from file_entity f WITH (NOLOCK)
    INNER JOIN file_content fc WITH (NOLOCK)
    ON fc.id=f.id
    LEFT JOIN link l WITH (NOLOCK)
    ON l.ref_id = f.id and l.document_id is not null
    LEFT JOIN link_statistic s WITH (NOLOCK)
    ON s.link_id = l.id
    WHERE f.created_by=@userId and f.company_id=@companyId
    GROUP BY f.id, f.created_date, fc.modified_date
    HAVING ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.modified_date), ISNULL(fc.modified_date, f.created_date))) > DATEADD(DAY, -@dateRecent, GETDATE())

    SELECT TOP(@top) f.id as id, f.display_name as displayName, CONVERT(DATETIME2, ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.modified_date), ISNULL(fc.modified_date, f.created_date))), 1) as recentActivityDate,
                     f.size as size, CASE WHEN COUNT(p.page) = 0.0 THEN 0 ELSE (COUNT(CASE WHEN p.duration > 0 THEN p.page ELSE NULL END) * 100/COUNT(p.page)) END as viewedRate, fv.views as views
    from file_entity f WITH (NOLOCK)
    INNER JOIN file_content fc WITH (NOLOCK)
    ON fc.id=f.id
    LEFT JOIN #fileWithViews fv
    ON fv.fileId = f.id
    LEFT JOIN link l WITH (NOLOCK)
    ON l.ref_id = f.id and l.document_id is not null
    LEFT JOIN link_statistic s WITH (NOLOCK)
    ON s.link_id=l.id
    LEFT JOIN page_statistic p WITH (NOLOCK)
    ON p.link_id = l.id
    WHERE f.created_by=@userId and f.company_id=@companyId
    GROUP BY f.id, f.display_name, f.created_date, f.size, fc.modified_date, fv.views
    HAVING ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.modified_date), ISNULL(fc.modified_date, f.created_date))) > DATEADD(DAY, -@dateRecent, GETDATE())
    ORDER BY
        CASE WHEN @sortDirection='DESC' THEN ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.modified_date), ISNULL(fc.modified_date, f.created_date))) END DESC,
        CASE WHEN @sortDirection='ASC' THEN ISNULL(MAX(s.viewed_at), ISNULL(MAX(l.modified_date), ISNULL(fc.modified_date, f.created_date))) END ASC

    If(OBJECT_ID('tempdb..#fileWithViews') Is Not Null)
    Begin
        Drop Table #fileWithViews
    End

END
GO
