
ALTER TABLE link_statistic
    DROP COLUMN latitude
GO

ALTER TABLE link_statistic
    DROP COLUMN longitude
GO

ALTER TABLE link_statistic
    ADD latitude float(53)
GO

ALTER TABLE link_statistic
    ADD longitude float(53)
GO