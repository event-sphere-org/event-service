-- Set search path to event_service_schema
SET
search_path TO event_service_schema;

-- Insert rows into the category table
INSERT INTO category (name, created_at, updated_at)
VALUES ('Music', current_timestamp, current_timestamp),
       ('Art', current_timestamp, current_timestamp),
       ('Food', current_timestamp, current_timestamp),
       ('Sport', current_timestamp, current_timestamp),
       ('Fashion', current_timestamp, current_timestamp),
       ('Technology', current_timestamp, current_timestamp);