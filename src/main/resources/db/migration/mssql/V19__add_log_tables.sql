CREATE TABLE logging_event
(
    timestmp         DECIMAL(20) NOT NULL,
    formatted_message  VARCHAR(max) NOT NULL,
    logger_name       VARCHAR(512) NOT NULL,
    level_string      VARCHAR(512) NOT NULL,
    thread_name       VARCHAR(512),
    reference_flag    SMALLINT,
    arg0              VARCHAR(512),
    arg1              VARCHAR(512),
    arg2              VARCHAR(512),
    arg3              VARCHAR(512),
    caller_filename   VARCHAR(512) NOT NULL,
    caller_class      VARCHAR(512) NOT NULL,
    caller_method     VARCHAR(512) NOT NULL,
    caller_line       CHAR(16) NOT NULL,
    event_id          DECIMAL(38) NOT NULL identity,
    PRIMARY KEY(event_id)
  )

GO

CREATE TABLE logging_event_property
(
    event_id          DECIMAL(38) NOT NULL,
    mapped_key        VARCHAR(512) NOT NULL,
    mapped_value      VARCHAR(1024),
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
)

GO

CREATE TABLE logging_event_exception
(
    event_id         DECIMAL(38) NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR(512) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
)
GO