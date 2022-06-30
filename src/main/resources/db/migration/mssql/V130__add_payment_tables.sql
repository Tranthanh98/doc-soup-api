CREATE TABLE payment_history
(
    id                      bigint IDENTITY (1, 1) NOT NULL,
    company_id              uniqueidentifier NOT NULL,
    quantity                int              NOT NULL,
    price                   decimal(18, 0)   NOT NULL,
    created_date            datetimeoffset   NOT NULL,
    status                  varchar(255),
    total_amount            decimal(18, 0)   NOT NULL,
    sub_total_amount        decimal(18, 0)   NOT NULL,
    currency                varchar(255),
    subscription_paypal_id  nvarchar(200),
    paypal_plan_id          nvarchar(200) NOT NULL,
    pay_pal_payment_payload nvarchar( MAX),
    CONSTRAINT pk_payment_history PRIMARY KEY (id)
)
GO

CREATE TABLE subscription
(
    company_id                  uniqueidentifier NOT NULL,
    created_date                datetimeoffset   NOT NULL,
    modified_date               datetimeoffset,
    created_by                  varchar(36)      NOT NULL,
    modified_by                 varchar(36),
    account_id                  varchar(36),
    sub_type                    varchar(255),
    plan_tier_id                bigint           NOT NULL,
    paypal_plan_id              nvarchar(200) NOT NULL,
    subscription_paypal_id      nvarchar(200),
    paypal_subscription_payload nvarchar( MAX),
    notes                       nvarchar(400),
    CONSTRAINT pk_subscription PRIMARY KEY (company_id)
)
GO

CREATE TABLE subscription_history
(
    id                          bigint IDENTITY (1, 1) NOT NULL,
    account_id                  varchar(36),
    company_id                  uniqueidentifier NOT NULL,
    sub_type                    varchar(255),
    plan_tier_id                bigint           NOT NULL,
    paypal_plan_id              nvarchar(200),
    subscription_paypal_id      nvarchar(200),
    paypal_subscription_payload nvarchar( MAX),
    notes                       nvarchar(400),
    created_date                datetimeoffset   NOT NULL,
    modified_date               datetimeoffset,
    created_by                  varchar(36)      NOT NULL,
    modified_by                 varchar(36),
    history_date                datetimeoffset   NOT NULL,
    CONSTRAINT pk_subscription_history PRIMARY KEY (id)
)
GO


