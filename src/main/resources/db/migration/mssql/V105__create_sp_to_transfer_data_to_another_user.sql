GO
CREATE PROCEDURE [dbo].[transfer_data_to_another_user]
    @sourceAccountId NVARCHAR(36),
    @destinationAccountId NVARCHAR(36)
AS
SET NOCOUNT ON
BEGIN
    BEGIN TRANSACTION;
    SAVE TRANSACTION TransferDataTransaction;

    BEGIN TRY
        -- data room
        UPDATE data_room
        SET account_id = @destinationAccountId
        WHERE account_id=@sourceAccountId

        UPDATE data_room
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE data_room
        SET modified_by = @destinationAccountId
        WHERE modified_by=@sourceAccountId

        -- data_room_user
        UPDATE data_room_user
        SET user_id = @destinationAccountId
        WHERE user_id=@sourceAccountId

        UPDATE data_room_user
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        -- data_room_content
        UPDATE data_room_content
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE data_room_content
        SET modified_by = @destinationAccountId
        WHERE modified_by = @sourceAccountId

        -- contact
        UPDATE contact
        SET account_id = @destinationAccountId
        WHERE account_id=@sourceAccountId

        UPDATE contact
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE contact
        SET modified_by = @destinationAccountId
        WHERE modified_by=@sourceAccountId

        -- directory
        UPDATE directory_entity
        SET account_id = @destinationAccountId
        WHERE account_id=@sourceAccountId

        UPDATE directory_entity
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE directory_entity
        SET modified_by = @destinationAccountId
        WHERE modified_by=@sourceAccountId

        -- document
        UPDATE document
        SET created_by = @destinationAccountId
        WHERE created_by=@sourceAccountId

        -- file_content
        UPDATE file_content
        SET created_by =@destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE file_content
        SET modified_by =@destinationAccountId
        WHERE modified_by=@sourceAccountId

        -- file_entity
        UPDATE file_entity
        SET account_id =@destinationAccountId
        WHERE account_id=@sourceAccountId

        UPDATE file_entity
        SET created_by =@destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE file_entity
        SET modified_by =@destinationAccountId
        WHERE modified_by=@sourceAccountId

        -- link
        UPDATE link
        SET created_by =@destinationAccountId
        WHERE created_by=@sourceAccountId

        -- watermark
        UPDATE watermark
        SET created_by =@destinationAccountId
        WHERE created_by=@sourceAccountId

        UPDATE watermark
        SET modified_by =@destinationAccountId
        WHERE modified_by=@sourceAccountId

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
    IF @@TRANCOUNT > 0
    BEGIN
    ROLLBACK TRANSACTION TransferDataTransaction;
    END
    END CATCH
END
GO
