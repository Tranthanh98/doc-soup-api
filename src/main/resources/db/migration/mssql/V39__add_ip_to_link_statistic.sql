ALTER TABLE link_statistic
    ADD ip varchar(45)
GO

ALTER TABLE link_statistic
    DROP COLUMN device_agent
GO

ALTER TABLE link_statistic
    ADD device_agent nvarchar(MAX) NOT NULL
GO