CREATE TABLE account
(
    id            varchar(36)   NOT NULL,
    first_name    nvarchar(50),
    last_name     nvarchar(50),
    email         nvarchar(200) NOT NULL,
    check_in_time datetime,
    CONSTRAINT pk_account PRIMARY KEY (id)
)
GO

CREATE UNIQUE NONCLUSTERED INDEX IX_ACCOUNT_EMAIL ON account (email)
GO
