ALTER TABLE plan_tier
    ADD monthly_fixed_plan_paypal_id varchar(255)
    GO

ALTER TABLE plan_tier
    ADD yearly_discount float(53)
    GO

ALTER TABLE plan_tier
    ADD yearly_fixed_plan_paypal_id varchar(255)
    GO

ALTER TABLE plan_tier
    ADD yearly_plan_paypal_id varchar(255)
    GO

ALTER TABLE plan_tier
    ADD monthly_plan_paypal_id varchar(255)
    GO

ALTER TABLE billing_info
    ADD subscription_paypal_id varchar(255)
    GO

CREATE
NONCLUSTERED INDEX IX_BILLING_INFO_SUBSCRIPTION_PAYPAL_ID ON billing_info (subscription_paypal_id)
GO

DROP INDEX [IX_BILLING_INFO_PAYMENT_ID_PAYER_ID] ON [dbo].[billing_info]
    GO

DROP TABLE promotion
    GO

ALTER TABLE billing_info
DROP
COLUMN payer_id
GO

ALTER TABLE billing_info
DROP
COLUMN payment_id
GO
