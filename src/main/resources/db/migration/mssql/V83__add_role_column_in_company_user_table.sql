ALTER TABLE company_user
ADD role varchar(50)
GO

UPDATE company_user
SET role = 'c_admin'
where member_type = 0
GO

UPDATE company_user
SET role = 'c_member'
where member_type = 1
GO

ALTER TABLE company_user
ALTER
COLUMN role varchar(50) NOT NULL
GO
