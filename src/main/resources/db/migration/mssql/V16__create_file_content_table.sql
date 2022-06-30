CREATE TABLE file_content
(
    id      bigint NOT NULL,
    content varbinary(MAX) not null ,
    CONSTRAINT pk_file_content PRIMARY KEY (id)
)
GO
