CREATE TABLE denied_visit
(
    id         bigint IDENTITY (1, 1) NOT NULL,
    link_id    uniqueidentifier,
    visit_time datetimeoffset,
    email      varchar(255),
    sent_email bit,
    CONSTRAINT pk_denied_visit PRIMARY KEY (id)
)
    GO

ALTER TABLE link_statistic
    ADD from_allow_viewers_link bit
    GO

ALTER TABLE link_statistic
    ADD sent_information_email bit
    GO
