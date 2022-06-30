CREATE TABLE directory_entity
(
    id        bigint IDENTITY (1, 1) NOT NULL,
    parent_id bigint                 NOT NULL,
    level     int                    NOT NULL,
    name      nvarchar(255)           NOT NULL,
    CONSTRAINT pk_directory_entity PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX DIRECTORY_ENTITY_NAME ON directory_entity (name ASC)
INCLUDE ( level,parent_id)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
    SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
    ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
GO

CREATE NONCLUSTERED INDEX DIRECTORY_ENTITY_PARENTID ON directory_entity (parent_id ASC)
    INCLUDE (name,level)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF,
        SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO