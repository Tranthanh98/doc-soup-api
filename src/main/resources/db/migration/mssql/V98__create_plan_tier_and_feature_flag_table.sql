CREATE TABLE feature_flag
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)    NOT NULL,
    modified_by   varchar(36),
    feature_key   varchar(255)   NOT NULL,
    plan_tier_id  bigint         NOT NULL,
    limit         int,
    unit          varchar(255),
    CONSTRAINT pk_feature_flag PRIMARY KEY (id)
)
GO

CREATE TABLE plan_tier
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)    NOT NULL,
    modified_by   varchar(36),
    name          nvarchar(255) NOT NULL,
    level         bigint         NOT NULL,
    initial_fee   decimal(18, 0) NOT NULL,
    initial_seat  int,
    seat_price    decimal(18, 0),
    CONSTRAINT pk_plan_tier PRIMARY KEY (id)
)
GO

ALTER TABLE company
ADD plan_tier_id bigint
GO

