CREATE TABLE data_room (
  id bigint IDENTITY (1, 1) NOT NULL,
  created_date datetimeoffset NOT NULL,
  modified_date datetimeoffset NOT NULL,
  account_id varchar(36) NOT NULL,
  name varchar(255) NOT NULL,
  is_active bit NOT NULL,
  CONSTRAINT pk_data_room PRIMARY KEY (id)
) GO

CREATE TABLE data_room_content (
  id bigint IDENTITY (1, 1) NOT NULL,
  created_date datetimeoffset NOT NULL,
  data_room_id bigint,
  directory_id bigint,
  file_id bigint,
  CONSTRAINT pk_data_room_content PRIMARY KEY (id)
) GO

CREATE NONCLUSTERED INDEX IX_DATA_ROOM_CONTENT_DATAROOMID ON data_room_content(data_room_id)
GO