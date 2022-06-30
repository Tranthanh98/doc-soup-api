CREATE TABLE internal_account
(
    id            varchar(36) NOT NULL,
    email         nvarchar(200) NOT NULL,
    check_in_time datetime    NOT NULL,
    CONSTRAINT pk_internal_account PRIMARY KEY (id)
)
    GO

CREATE
UNIQUE
NONCLUSTERED INDEX IX_ACCOUNT_EMAIL ON internal_account (email)
GO
