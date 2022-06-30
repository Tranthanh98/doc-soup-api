ALTER TABLE plan_tier
    ADD description nvarchar(255)
GO

ALTER TABLE plan_tier
    ADD is_active bit
    GO

update plan_tier
set is_active = 1

ALTER TABLE plan_tier
ALTER
COLUMN is_active bit NOT NULL
GO
