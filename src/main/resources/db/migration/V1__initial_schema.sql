SET search_path TO ptms, public;

CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE buses (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(15) NOT NULL UNIQUE,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year_of_manufacture INTEGER NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL,
    seating_capacity INTEGER NOT NULL,
    standing_capacity INTEGER NOT NULL,
    ntc_permit_number BIGINT NOT NULL UNIQUE,
    comfort_type VARCHAR(20) NOT NULL,
    is_ac BOOLEAN NOT NULL,
    service_type VARCHAR(20) NOT NULL
);
CREATE INDEX idx_buses_active ON buses (is_active);
CREATE INDEX idx_buses_service_type ON buses (service_type);

CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    nic_number VARCHAR(12) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    contact_number VARCHAR(10) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    date_joined DATE NOT NULL,
    is_current_employee BOOLEAN NOT NULL,
    driving_license_number VARCHAR(15) NOT NULL UNIQUE,
    license_expiration_date DATE NOT NULL,
    license_class VARCHAR(30) NOT NULL,
    ntc_license_number VARCHAR(15) UNIQUE,
    ntc_license_expiration_date DATE,
    available BOOLEAN NOT NULL
);
CREATE INDEX idx_drivers_available ON drivers (available);

CREATE TABLE conductors (
    id BIGSERIAL PRIMARY KEY,
    nic_number VARCHAR(12) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    contact_number VARCHAR(10) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    date_joined DATE NOT NULL,
    is_current_employee BOOLEAN NOT NULL,
    conductor_license_number VARCHAR(15) NOT NULL UNIQUE,
    license_expiration_date DATE NOT NULL,
    available BOOLEAN NOT NULL
);
CREATE INDEX idx_conductors_available ON conductors (available);

CREATE TABLE routes (
    id BIGSERIAL PRIMARY KEY,
    route_number VARCHAR(20) NOT NULL UNIQUE,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    route_path geography(LineString,4326)
);
CREATE INDEX idx_routes_origin ON routes (origin);
CREATE INDEX idx_routes_destination ON routes (destination);

CREATE TABLE route_stops (
    route_id BIGINT NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_order INTEGER NOT NULL,
    stop_name VARCHAR(150) NOT NULL,
    PRIMARY KEY (route_id, stop_order)
);

CREATE TABLE scheduled_trips (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES routes(id),
    direction VARCHAR(10) NOT NULL,
    expected_start_time TIME NOT NULL,
    expected_end_time TIME NOT NULL
);
CREATE INDEX idx_scheduled_trips_route ON scheduled_trips (route_id);
CREATE INDEX idx_scheduled_trips_direction ON scheduled_trips (direction);

CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    scheduled_trip_id BIGINT NOT NULL REFERENCES scheduled_trips(id),
    bus_id BIGINT NOT NULL REFERENCES buses(id),
    driver_id BIGINT NOT NULL REFERENCES drivers(id),
    conductor_id BIGINT NOT NULL REFERENCES conductors(id),
    assignment_date DATE NOT NULL,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL
);
CREATE INDEX idx_assignments_date ON assignments (assignment_date);
CREATE INDEX idx_assignments_status ON assignments (status);
CREATE INDEX idx_assignments_trip ON assignments (scheduled_trip_id);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nic VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE action_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    revision_type VARCHAR(20) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    changes JSONB,
    timestamp TIMESTAMP NOT NULL
);
CREATE INDEX idx_action_logs_entity ON action_logs (entity_type, entity_id);
CREATE INDEX idx_action_logs_timestamp ON action_logs (timestamp);

-- Kept only so the existing audit model still validates during Phase 1.
-- Envers event integration itself is intentionally disabled until Phase 3.
CREATE TABLE custom_revision_entity (
    id SERIAL PRIMARY KEY,
    timestamp BIGINT NOT NULL,
    username VARCHAR(100)
);

CREATE TABLE rev_modified_entities (
    rev INTEGER NOT NULL REFERENCES custom_revision_entity(id) ON DELETE CASCADE,
    entity_name VARCHAR(255) NOT NULL
);
