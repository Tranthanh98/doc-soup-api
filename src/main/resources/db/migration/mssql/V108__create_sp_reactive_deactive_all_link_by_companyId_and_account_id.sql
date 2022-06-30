GO
CREATE PROCEDURE [dbo].[deactive_all_link_by_account_id_and_company_id]
    @accountId NVARCHAR(36),
    @companyId NVARCHAR(36)
AS
    SET NOCOUNT ON
    BEGIN
    BEGIN TRANSACTION;
    SAVE TRANSACTION DeactiveAllLinkTransaction;

    BEGIN TRY
        UPDATE link
        SET status = status + 1
        WHERE created_by=@accountId and company_id=@companyId

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
    IF @@TRANCOUNT > 0
    BEGIN
    ROLLBACK TRANSACTION DeactiveAllLinkTransaction;
    END
    END CATCH
END

GO
CREATE PROCEDURE [dbo].[reactive_all_link_by_account_id_and_company_id]
    @accountId NVARCHAR(36),
    @companyId NVARCHAR(36)
AS
    SET NOCOUNT ON
    BEGIN
    BEGIN TRANSACTION;
    SAVE TRANSACTION DeactiveAllLinkTransaction;

    BEGIN TRY
        UPDATE link
        SET status = status - 1
        WHERE created_by=@accountId and company_id=@companyId

        COMMIT TRANSACTION
    END TRY
    BEGIN CATCH
    IF @@TRANCOUNT > 0
    BEGIN
    ROLLBACK TRANSACTION DeactiveAllLinkTransaction;
    END
    END CATCH
    END
GO

