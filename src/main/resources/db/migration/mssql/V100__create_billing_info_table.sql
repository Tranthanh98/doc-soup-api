CREATE TABLE billing_info
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset   NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)      NOT NULL,
    modified_by   varchar(36),
    account_id    varchar(36),
    company_id    uniqueidentifier NOT NULL,
    seat          int              NOT NULL,
    price         decimal(18, 0)   NOT NULL,
    status        int              NOT NULL,
    total_amount  decimal(18, 0)   NOT NULL,
    next_bill     datetimeoffset   NOT NULL,
    sub_type      varchar(255),
    notes         varchar(255),
    plan_tier_id  bigint           NOT NULL,
    CONSTRAINT pk_billing_info PRIMARY KEY (id)
)
GO