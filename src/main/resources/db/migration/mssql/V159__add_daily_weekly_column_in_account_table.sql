ALTER TABLE account
ADD send_daily_summary bit
GO

ALTER TABLE account
ADD send_weekly_summary bit
GO

UPDATE account SET send_daily_summary = 1, send_weekly_summary = 1

ALTER TABLE account
ALTER
COLUMN send_daily_summary bit NOT NULL
GO

ALTER TABLE account
ALTER
COLUMN send_weekly_summary bit NOT NULL
GO
