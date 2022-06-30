CREATE TABLE link_accounts
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset   NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)      NOT NULL,
    modified_by   varchar(36),
    name          nvarchar(255) NOT NULL,
    company_id    uniqueidentifier NOT NULL,
    archived      bit,
    CONSTRAINT pk_link_accounts PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX IX_LINK_ACCOUNTS_NAME_COMPANY_ID ON link_accounts (name, company_id)
GO

INSERT INTO link_accounts (name, company_id, archived, created_by, created_date, modified_date)
SELECT l.name, l.company_id, 0, l.created_by, l.created_date, l.modified_date
FROM link l, (SELECT MAX(link.id) as id, link.company_id as company_id, link.name as name FROM link GROUP BY link.name, link.company_id) as x
WHERE l.id = x.id

ALTER TABLE link
    ADD link_accounts_id bigint
GO

UPDATE link
SET link.link_accounts_id = (SELECT id from link_accounts la WHERE link.name = la.name and link.company_id = la.company_id)

ALTER TABLE link
ALTER
COLUMN link_accounts_id bigint NOT NULL
GO

ALTER TABLE link
DROP COLUMN name
GO

CREATE
NONCLUSTERED INDEX IX_LINK_LINK_ACCOUNTS_ID ON link (link_accounts_id)
GO
