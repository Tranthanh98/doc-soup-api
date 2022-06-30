CREATE TABLE data_room_user
(
    id           bigint IDENTITY (1, 1) NOT NULL,
    data_room_id bigint         NOT NULL,
    user_id      varchar(36),
    created_date datetimeoffset NOT NULL,
    created_by   varchar(36)    NOT NULL,
    CONSTRAINT pk_data_room_user PRIMARY KEY (id)
)
    GO

CREATE
NONCLUSTERED INDEX IX_DATA_ROOM_USER_DATAROOMID ON data_room_user (data_room_id)
GO

CREATE
NONCLUSTERED INDEX IX_DATA_ROOM_USER_USERID ON data_room_user (user_id)
GO
