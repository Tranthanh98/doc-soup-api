ALTER TABLE link_statistic
    ADD download_file_token varchar(36)
GO

CREATE PROCEDURE [dbo].[get_all_file_of_data_room_content]
    @dataRoomId bigint
AS
BEGIN

    SET NOCOUNT ON;

    DECLARE @temp_directory TABLE (
        id BIGINT,
        name nvarchar(255)
    ) ;

    WITH directory_temp(id, name) AS(
        SELECT directory_id AS id, CAST(de.name as nvarchar(max))
        FROM data_room_content AS drc WITH(NOLOCK)
        INNER JOIN directory_entity AS de WITH(NOLOCK)
        ON de.id = drc.directory_id
        WHERE drc.directory_id IS NOT NULL
        AND drc.data_room_id = @dataRoomId
        UNION ALL
        SELECT d2.id, CONCAT(d1.name,'/', d2.name) as name
        FROM directory_temp AS d1, directory_entity AS d2
        WHERE d1.id = d2.parent_id
    )
    INSERT @temp_directory
    SELECT id, name FROM directory_temp;

    SELECT drc.file_id AS id, cast( fe.display_name as nvarchar(max)) AS name, fe.extension
    FROM data_room_content AS drc WITH(NOLOCK)
    JOIN file_entity AS fe WITH(NOLOCK) ON drc.file_id = fe.id
    WHERE data_room_id = @dataRoomId
    AND file_id IS NOT NULL
    UNION ALL
    SELECT fe1.id, CONCAT(temp.name, '/', fe1.display_name) AS name, fe1.extension
    FROM file_entity as fe1 WITH(NOLOCK)
    RIGHT JOIN @temp_directory as temp ON fe1.directory_id = temp.id
END
GO