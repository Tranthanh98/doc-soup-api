ALTER TABLE demo_entity
    ADD fullname varchar(255)
GO

ALTER TABLE demo_entity
    ALTER COLUMN fullname varchar(255) NOT NULL
GO

ALTER TABLE demo_entity
    DROP COLUMN full_name
GO