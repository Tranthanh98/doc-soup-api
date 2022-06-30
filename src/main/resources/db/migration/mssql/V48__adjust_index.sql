DROP INDEX IX_DATA_ROOM_CONTENT_DOCUMENTID ON data_room_content
GO

ALTER TABLE data_room_content
DROP
COLUMN document_id
GO
