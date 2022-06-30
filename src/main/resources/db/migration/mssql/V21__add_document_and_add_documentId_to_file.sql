CREATE TABLE document
(
    id                uniqueidentifier NOT NULL,
    created_date      datetimeoffset   NOT NULL,
    modified_date     datetimeoffset,
    created_by        varchar(36)      NOT NULL,
    modified_by       varchar(36),
    alink             varchar(255),
    created_at        varchar(255),
    crypted           bit,
    deleted           bit,
    doc_name          varchar(255),
    external_id       varchar(255),
    file_size         bigint,
    given_name        varchar(255),
    has_password      bit,
    is_empty_doc_name bit,
    secure_id         varchar(255),
    streamdocs_id     varchar(255),
    type              varchar(255),
    updated_at        varchar(255),
    expired_at        datetimeoffset,
    ref_id            uniqueidentifier,
    CONSTRAINT pk_document PRIMARY KEY (id)
)
    GO

ALTER TABLE file_entity
    ADD doc_expired_at datetimeoffset
    GO

ALTER TABLE file_entity
    ADD document_id uniqueidentifier
    GO

CREATE
NONCLUSTERED INDEX IX_DOCUMENT_EXPIREDAT ON document (expired_at)
GO

CREATE
NONCLUSTERED INDEX IX_DOCUMENT_REFID ON document (ref_id)
GO

CREATE
NONCLUSTERED INDEX IX_FILE_ENTITY_DOCEXPIREDAT ON file_entity (doc_expired_at)
INCLUDE (document_id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

CREATE
NONCLUSTERED INDEX IX_FILE_ENTITY_DOCUMENTID ON file_entity (document_id)
 INCLUDE (doc_expired_at)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
