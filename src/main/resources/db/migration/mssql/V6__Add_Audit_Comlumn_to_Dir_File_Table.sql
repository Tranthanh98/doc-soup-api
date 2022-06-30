
Delete from directory_entity
GO

Delete from file_entity

Go

ALTER TABLE directory_entity
    ADD
        account_id varchar(36) not null,
        created_date datetimeoffset not null,
        modified_date datetimeoffset not null

GO

ALTER TABLE file_entity
    ADD account_id varchar(36) not null,
        created_date datetimeoffset not null,
        modified_date datetimeoffset not null
GO

CREATE NONCLUSTERED INDEX DIRECTORY_ENTITY_ACCOUNT ON directory_entity (account_id)
    INCLUDE (name)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX FILE_ENTITY_ACCOUNT ON file_entity (account_id)
    INCLUDE (name)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
