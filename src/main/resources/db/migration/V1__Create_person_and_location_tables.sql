CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    reference_id BIGINT NOT NULL,
    geom GEOMETRY(Point, 4326) NOT NULL,
    CONSTRAINT fk_person FOREIGN KEY(reference_id) REFERENCES persons(id)
);

-- Create a spatial index on the geometry column
CREATE INDEX idx_locations_geom ON locations USING GIST (geom);