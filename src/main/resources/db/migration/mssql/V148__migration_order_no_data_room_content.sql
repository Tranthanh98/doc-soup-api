declare @AllDataRoomId table (id bigint);
DECLARE @id int;

insert into @AllDataRoomId
select data_room_id from data_room_content
group by data_room_id

DECLARE cursorDataRoom CURSOR FOR
select id from @AllDataRoomId;

OPEN cursorDataRoom
fetch NEXT from cursorDataRoom into @id
while @@FETCH_STATUS = 0
BEGIN

    UPDATE x
    SET x.order_no = x.newOrder
        FROM (
            SELECT order_no, data_room_id, ROW_NUMBER() OVER (ORDER BY [created_date]) AS newOrder
            FROM data_room_content WITH(NOLOCK)
            where data_room_id = @id
        ) x
    WHERE x.order_no <> x.newOrder AND x.order_no IS NOT NULL
    and x.data_room_id = @id
    FETCH NEXT FROM cursorDataRoom INTO @id

END;

CLOSE cursorDataRoom
DEALLOCATE cursorDataRoom;