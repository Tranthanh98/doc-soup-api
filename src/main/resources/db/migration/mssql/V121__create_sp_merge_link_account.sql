GO
CREATE PROCEDURE [dbo].[merge_link_account]
    @sourceLinkAccountId BIGINT,
    @destinationLinkAccountId BIGINT
AS
SET NOCOUNT ON
BEGIN

    BEGIN TRANSACTION;
    SAVE TRANSACTION MergeLinkAccountTransaction;

    BEGIN TRY
        UPDATE link WITH(ROWLOCK, UPDLOCK)
        SET link_accounts_id = @destinationLinkAccountId
        WHERE link_accounts_id = @sourceLinkAccountId

        DELETE
        FROM link_accounts WITH(ROWLOCK, UPDLOCK)
        WHERE id = @sourceLinkAccountId

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
    IF @@TRANCOUNT > 0
    BEGIN
    ROLLBACK TRANSACTION MergeLinkAccountTransaction;
    END
    END CATCH
END
GO