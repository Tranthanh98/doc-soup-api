ALTER TABLE company_user
ADD invitation_status int
GO

UPDATE company_user
SET invitation_status = 1
GO

ALTER TABLE company_user
ALTER
COLUMN invitation_status int NOT NULL
GO

ALTER TABLE company_user
ADD token varchar(255)
GO

CREATE
NONCLUSTERED INDEX IX_COMPANY_USER_TOKEN ON company_user (token)
GO