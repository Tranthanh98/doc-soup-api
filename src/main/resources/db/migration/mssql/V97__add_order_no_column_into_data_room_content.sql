ALTER TABLE data_room_content
    ADD order_no int  NOT NULL default 0
    GO

CREATE PROCEDURE [dbo].[upd_increase_order_no_dataroom_content]
    @contentId int,
    @afterId int
AS
BEGIN
    DECLARE @replacedOrderNo INT;

    SELECT @replacedOrderNo = order_no FROM data_room_content WITH(NOLOCK) WHERE id = @afterId;

    UPDATE t
    SET t.order_no = t.order_no + 1
    FROM data_room_content AS t WITH(ROWLOCK,UPDLOCK)
    WHERE t.order_no >= @replacedOrderNo
    AND t.order_no <= (SELECT order_no FROM data_room_content WITH(NOLOCK) WHERE id = @contentId);

    UPDATE data_room_content WITH(ROWLOCK,UPDLOCK)
    SET order_no = @replacedOrderNo
    WHERE id = @contentId
END
GO

CREATE PROCEDURE [dbo].[upd_decrease_order_no_dataroom_content]
    @contentId int,
    @beforeId int
AS
BEGIN
    DECLARE @replacedOrderNo INT;

    SELECT @replacedOrderNo = order_no FROM data_room_content WITH(NOLOCK) WHERE id = @beforeId;

    UPDATE t
    SET t.order_no = t.order_no - 1
    FROM data_room_content AS t WITH(ROWLOCK,UPDLOCK)
    WHERE t.order_no > (SELECT order_no FROM data_room_content WITH(NOLOCK) WHERE id = @contentId)
    AND t.order_no < @replacedOrderNo + 1;

    UPDATE data_room_content WITH(ROWLOCK,UPDLOCK)
    SET order_no = @replacedOrderNo
    WHERE id = @contentId
END
GO

--migration first data
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
            FROM data_room_content
            where data_room_id = @id
            ) x
        WHERE x.order_no <> x.newOrder AND x.order_no IS NOT NULL
        and x.data_room_id = @id

        FETCH NEXT FROM cursorDataRoom INTO @id
    END;

CLOSE cursorDataRoom           
DEALLOCATE cursorDataRoom;