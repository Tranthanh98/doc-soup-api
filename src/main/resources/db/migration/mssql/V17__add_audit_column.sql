ALTER TABLE data_room
    ADD created_by varchar(36)
GO

ALTER TABLE data_room
    ADD modified_by varchar(36)
GO

ALTER TABLE data_room
    ALTER COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE data_room_content
    ADD created_by varchar(36)
GO

ALTER TABLE data_room_content
    ADD modified_by varchar(36)
GO

ALTER TABLE data_room_content
    ADD modified_date datetimeoffset
GO

ALTER TABLE data_room_content
    ALTER COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE directory_entity
    ADD created_by varchar(36)
GO

ALTER TABLE directory_entity
    ADD modified_by varchar(36)
GO

ALTER TABLE directory_entity
    ALTER COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE file_content
    ADD created_by varchar(36)
GO

ALTER TABLE file_content
    ADD created_date datetimeoffset
GO

ALTER TABLE file_content
    ADD modified_by varchar(36)
GO

ALTER TABLE file_content
    ADD modified_date datetimeoffset
GO

ALTER TABLE file_content
    ALTER COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE file_entity
    ADD created_by varchar(36)
GO

ALTER TABLE file_entity
    ADD modified_by varchar(36)
GO

ALTER TABLE file_entity
    ALTER COLUMN created_by varchar(36) NOT NULL
GO

ALTER TABLE file_content
    ALTER COLUMN created_date datetimeoffset NOT NULL
GO

ALTER TABLE data_room
    ALTER COLUMN modified_date datetimeoffset NULL
GO

ALTER TABLE directory_entity
    ALTER COLUMN modified_date datetimeoffset NULL
GO

ALTER TABLE file_entity
    ALTER COLUMN modified_date datetimeoffset NULL
GO