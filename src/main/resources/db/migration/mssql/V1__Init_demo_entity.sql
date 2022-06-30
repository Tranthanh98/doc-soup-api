CREATE TABLE demo_entity
(
    id        bigint IDENTITY (1, 1) NOT NULL,
    full_name varchar(255)           NOT NULL,
    CONSTRAINT pk_demoentity PRIMARY KEY (id)
)
GO