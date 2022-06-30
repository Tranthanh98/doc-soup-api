CREATE TABLE promotion
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)    NOT NULL,
    modified_by   varchar(36),
    name          nvarchar(255) NOT NULL,
    apply_to      varchar(255),
    account_id    varchar(36)    NOT NULL,
    discount      float(53)      NOT NULL,
    start_date    datetimeoffset NOT NULL,
    end_date      datetimeoffset,
    CONSTRAINT pk_promotion PRIMARY KEY (id)
)
GO
