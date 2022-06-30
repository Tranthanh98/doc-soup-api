CREATE TABLE file_entity
(
    id           bigint IDENTITY (1, 1) NOT NULL,
    name         varchar(255)           NOT NULL,
    display_name nvarchar(255)          NOT NULL,
    extension    varchar(10)            NOT NULL,
    CONSTRAINT pk_fileentity PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX DIRECTORY_ENTITY_NAME ON file_entity (display_name)
    INCLUDE (name,extension)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
