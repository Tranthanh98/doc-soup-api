CREATE TABLE watermark
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    created_date  datetimeoffset         NOT NULL,
    modified_date datetimeoffset,
    created_by    varchar(36)            NOT NULL,
    modified_by   varchar(36),
    text          nvarchar(4000),
    image         varbinary(MAX)         NOT NULL,
    image_type    varchar(15)            NOT NULL,
    is_default    bit                    NOT NULL,
    account_id    varchar(36)            NOT NULL,
    CONSTRAINT pk_watermark PRIMARY KEY (id)
)
GO

CREATE NONCLUSTERED INDEX IX_WATERMARK_ACCOUNTID ON watermark (account_id)
GO