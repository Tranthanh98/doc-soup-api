ALTER TABLE account
    ADD created_by varchar(36)
    GO

ALTER TABLE account
    ADD created_date datetimeoffset
    GO

ALTER TABLE account
    ADD modified_by varchar(36)
    GO

ALTER TABLE account
    ADD modified_date datetimeoffset
    GO

UPDATE account
SET created_by = id,
    created_date = GETUTCDATE();

ALTER TABLE account
ALTER
COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE account
ALTER
COLUMN created_date datetimeoffset NOT NULL
GO

-- internal account
ALTER TABLE internal_account
    ADD created_by varchar(36)
    GO

ALTER TABLE internal_account
    ADD created_date datetimeoffset
    GO

ALTER TABLE internal_account
    ADD modified_by varchar(36)
    GO

ALTER TABLE internal_account
    ADD modified_date datetimeoffset
    GO

UPDATE internal_account
SET created_by = id,
    created_date = GETUTCDATE();

ALTER TABLE internal_account
ALTER
COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE internal_account
ALTER
COLUMN created_date datetimeoffset NOT NULL
GO

