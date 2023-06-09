-- Set search path to event_service_schema
SET search_path TO event_service_schema;

-- Create tables in event_service_schema
CREATE TABLE "event"
(
    id          BIGSERIAL PRIMARY KEY,
    creator_id  BIGINT,
    category_id BIGINT REFERENCES event_service_schema."category" (id),
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url   VARCHAR(255),
    location    VARCHAR(255),
    date        DATE,
    time        TIME WITH TIME ZONE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);