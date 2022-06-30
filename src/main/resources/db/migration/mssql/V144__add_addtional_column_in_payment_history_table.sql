ALTER TABLE payment_history
    ADD invoice nvarchar(500)
GO

ALTER TABLE payment_history
    ADD sent_invoice bit
    GO

UPDATE payment_history SET sent_invoice = 1

ALTER TABLE payment_history
ALTER
COLUMN sent_invoice bit NOT NULL
GO
