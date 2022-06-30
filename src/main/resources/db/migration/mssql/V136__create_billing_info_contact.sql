ALTER TABLE company
    ADD billing_contact nvarchar(150)
GO

ALTER TABLE company
    ADD billing_info_city nvarchar(150)
GO

ALTER TABLE company
    ADD billing_info_name nvarchar(150)
GO

ALTER TABLE company
    ADD billing_info_state nvarchar(150)
GO

ALTER TABLE company
    ADD billing_info_street nvarchar(150)
GO

ALTER TABLE company
    ADD billing_info_tax_id nvarchar(25)
GO

ALTER TABLE company
    ADD billing_info_zip_code nvarchar(25)
GO
