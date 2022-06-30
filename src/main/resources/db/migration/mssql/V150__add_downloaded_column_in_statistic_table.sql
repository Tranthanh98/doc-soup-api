ALTER TABLE link_statistic
ADD downloaded bit
GO

UPDATE link_statistic
SET downloaded = 0
GO

ALTER TABLE link_statistic
ALTER
COLUMN downloaded bit NOT NULL
GO
