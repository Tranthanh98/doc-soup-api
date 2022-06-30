CREATE TABLE history_visitor
(
    id        bigint IDENTITY (1, 1) NOT NULL,
    link_id   uniqueidentifier NOT NULL,
    viewer_id bigint           NOT NULL,
    timestamp datetimeoffset   NOT NULL,
    action    varchar(255)     NOT NULL,
    user_agent varchar(255)     NOT NULL
    CONSTRAINT pk_history_visitor PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX IX_HISTORY_VISITOR_LINKID_VIEWERID ON history_visitor (link_id, viewer_id)
GO

UPDATE link_statistic
SET nda_id = null

DELETE payment_history
WHERE invoice IS NULL

UPDATE file_entity
SET nda = 0
where nda = 1

ALTER TABLE link_statistic
    ADD bucket_key nvarchar(150)
GO
