-- Set search path to event_service_schema
SET search_path TO event_service_schema;

CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(50) UNIQUE NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
