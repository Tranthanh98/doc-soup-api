CREATE TABLE link
(
    id            uniqueidentifier NOT NULL,
    created_date  datetimeoffset   NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)      NOT NULL,
    modified_by   varchar(36),
    ref_id        bigint           NOT NULL,
    document_id   uniqueidentifier,
    visit         bigint           NOT NULL,
    name          nvarchar(255)    NOT NULL,
    CONSTRAINT pk_link PRIMARY KEY (id)
)
    GO

ALTER TABLE data_room_content
    ADD document_id uniqueidentifier
    GO

CREATE NONCLUSTERED INDEX IX_DATA_ROOM_CONTENT_DOCUMENTID ON data_room_content (document_id)
GO

CREATE NONCLUSTERED INDEX IX_LINK_DOCUMENTID ON link (document_id)
GO

CREATE NONCLUSTERED INDEX IX_LINK_REFID ON link (ref_id)
GO