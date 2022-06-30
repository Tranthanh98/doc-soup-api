
CREATE TABLE chat_log (
    id     uniqueidentifier NOT NULL,
    start_chat        datetimeoffset,
    property  VARCHAR(400) NOT NULL,
    visitor    VARCHAR(4000),
    end_chat        datetimeoffset,
    CONSTRAINT pk_chat_log PRIMARY KEY (id)
)
GO