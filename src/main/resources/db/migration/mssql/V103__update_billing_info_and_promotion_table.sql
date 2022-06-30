ALTER TABLE billing_info
ADD discount float(53)
GO

ALTER TABLE billing_info
ADD initial_fee decimal(18, 0)
GO

UPDATE billing_info
SET initial_fee = 0.0

ALTER TABLE billing_info
ALTER
COLUMN initial_fee decimal(18, 0) NOT NULL
GO

ALTER TABLE billing_info
ADD initial_fee_after_discount decimal(18, 0)
GO

ALTER TABLE billing_info
ADD initial_seat int
GO

ALTER TABLE billing_info
ADD payer_id varchar(255)
GO

ALTER TABLE billing_info
ADD payment_id varchar(255)
GO

ALTER TABLE billing_info
ADD price_after_discount decimal(18, 0)
GO

ALTER TABLE billing_info
ADD process_status int
GO

UPDATE billing_info
SET process_status = 0

ALTER TABLE billing_info
ALTER
COLUMN process_status int NOT NULL
GO

CREATE
NONCLUSTERED INDEX IX_BILLING_INFO_PAYMENT_ID_PAYER_ID ON billing_info (payment_id, payer_id)
GO

CREATE
NONCLUSTERED INDEX IX_PROMOTION_APPLY_TO ON promotion (apply_to)
GO
