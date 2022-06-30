ALTER TABLE link_statistic
    ADD authorized_at datetime
GO

ALTER TABLE link_statistic
    ADD device_id varchar(40) NOT NULL
GO

ALTER TABLE link_statistic
    ADD device_name nvarchar(50)
GO

ALTER TABLE link_statistic
    ADD location nvarchar(250)
GO

ALTER TABLE link_statistic
    ADD ndatoken varchar(36)
GO



CREATE NONCLUSTERED INDEX IX_LINK_STATISTIC_LINKID_DEVICEID ON link_statistic (link_id, device_id)
GO

ALTER TABLE link_statistic
    DROP COLUMN device_agent
GO

ALTER TABLE link_statistic
    ADD device_agent nvarchar(MAX) NOT NULL
GO
