--liquibase formatted sql

--changeset agalkin:1
CREATE TABLE IF NOT EXISTS notification_task  (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    notification_date_time TIMESTAMP NOT NULL
);
