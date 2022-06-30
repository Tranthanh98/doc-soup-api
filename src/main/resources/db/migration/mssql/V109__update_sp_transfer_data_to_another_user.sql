GO
ALTER PROCEDURE [dbo].[transfer_data_to_another_user]
    @sourceAccountId NVARCHAR(36),
    @destinationAccountId NVARCHAR(36),
    @companyId NVARCHAR(36)
AS
SET NOCOUNT ON
BEGIN
    BEGIN TRANSACTION;
    SAVE TRANSACTION TransferDataTransaction;

    BEGIN TRY
            -- data room
            UPDATE data_room
            SET account_id = @destinationAccountId
            WHERE account_id=@sourceAccountId and company_id=@companyId

            UPDATE data_room
            SET created_by = @destinationAccountId
            WHERE created_by=@sourceAccountId and company_id=@companyId

            UPDATE data_room
            SET modified_by = @destinationAccountId
            WHERE modified_by=@sourceAccountId and company_id=@companyId

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
            WHERE account_id=@sourceAccountId and company_id=@companyId

            UPDATE contact
            SET created_by = @destinationAccountId
            WHERE created_by=@sourceAccountId and company_id=@companyId

            UPDATE contact
            SET modified_by = @destinationAccountId
            WHERE modified_by=@sourceAccountId and company_id=@companyId

            -- directory
            UPDATE directory_entity
            SET account_id = @destinationAccountId
            WHERE account_id=@sourceAccountId and company_id=@companyId

            UPDATE directory_entity
            SET created_by = @destinationAccountId
            WHERE created_by=@sourceAccountId and company_id=@companyId

            UPDATE directory_entity
            SET modified_by = @destinationAccountId
            WHERE modified_by=@sourceAccountId and company_id=@companyId

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
            WHERE account_id=@sourceAccountId and company_id=@companyId

            UPDATE file_entity
            SET created_by =@destinationAccountId
            WHERE created_by=@sourceAccountId and company_id=@companyId

            UPDATE file_entity
            SET modified_by =@destinationAccountId
            WHERE modified_by=@sourceAccountId and company_id=@companyId

            -- link
            -- reactive link status
            UPDATE link
            SET status = status - 1
            WHERE created_by=@sourceAccountId and company_id=@companyId

            UPDATE link
            SET created_by =@destinationAccountId
            WHERE created_by=@sourceAccountId and company_id=@companyId

            -- watermark
            UPDATE watermark
            SET account_id =@destinationAccountId
            WHERE account_id=@sourceAccountId  and company_id=@companyId

            UPDATE watermark
            SET created_by =@destinationAccountId
            WHERE created_by=@sourceAccountId  and company_id=@companyId

            UPDATE watermark
            SET modified_by =@destinationAccountId
            WHERE modified_by=@sourceAccountId and company_id=@companyId

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
