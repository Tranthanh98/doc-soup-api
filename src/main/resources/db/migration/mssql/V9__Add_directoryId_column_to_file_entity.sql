ALTER TABLE file_entity
    ADD directory_id bigint
GO

CREATE NONCLUSTERED INDEX IX_FILE_ENTITY_DIRECTORYID ON file_entity (directory_id)
    INCLUDE (name,display_name,extension,size,account_id)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
