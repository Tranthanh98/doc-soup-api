ALTER TABLE payment_history
ADD discount float(53)
GO

ALTER TABLE payment_history
ADD initial_fee decimal(18, 0)
GO

UPDATE payment_history
SET initial_fee = 0.0, discount = 0.0
GO

ALTER TABLE payment_history
ALTER
COLUMN initial_fee decimal(18, 0) NOT NULL
GO
