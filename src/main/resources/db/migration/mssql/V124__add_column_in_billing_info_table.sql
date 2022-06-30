ALTER TABLE billing_info
ADD paypal_plan_id varchar(255)
GO

ALTER TABLE billing_info
ADD paypal_subscription_payload nvarchar(MAX)
GO
