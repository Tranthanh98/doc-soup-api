ALTER TABLE link_statistic
ADD nda_id bigint
GO

ALTER TABLE link_statistic
ADD signednda bit NOT NULL default 0
GO
