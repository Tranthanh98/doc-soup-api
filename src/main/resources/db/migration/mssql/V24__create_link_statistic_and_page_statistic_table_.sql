CREATE TABLE link_statistic
(
    id           bigint IDENTITY (1, 1) NOT NULL,
    link_id      uniqueidentifier,
    document_id  uniqueidentifier,
    visit        bigint                 NOT NULL,
    duration     bigint                 NOT NULL,
    last_page    int                    NOT NULL,
    total_page   int                    NOT NULL,
    longitude    bigint,
    latitude     bigint,
    device_agent varchar(255),
    contact_id   bigint,
    viewed_at    datetimeoffset,
    CONSTRAINT pk_link_statistic PRIMARY KEY (id)
)
    GO

CREATE TABLE page_statistic
(
    id                bigint IDENTITY (1, 1) NOT NULL,
    link_id           uniqueidentifier,
    link_statistic_id bigint,
    page              int                    NOT NULL,
    duration          bigint                 NOT NULL,
    CONSTRAINT pk_page_statistic PRIMARY KEY (id)
)
    GO

CREATE NONCLUSTERED INDEX IX_LINK_STATIC_LINKID ON link_statistic (link_id)
GO