ALTER TABLE page_statistic
    ADD session_id varchar(36)
GO

CREATE NONCLUSTERED INDEX IX_PAGE_STATISTIC_LINKSTATISTICID_PAGE ON page_statistic (link_statistic_id, page)
GO
