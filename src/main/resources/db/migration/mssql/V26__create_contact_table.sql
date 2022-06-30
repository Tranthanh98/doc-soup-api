CREATE TABLE contact
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset         NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)            NOT NULL,
    modified_by   varchar(36),
    account_id    varchar(36)            NOT NULL,
    email         varchar(255)           NOT NULL,
    name          varchar(255),
    CONSTRAINT pk_contact PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX IX_CONTACT_ENTITY_ACCOUNTID ON contact (account_id)
GO