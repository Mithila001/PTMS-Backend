-- Spring Data auditing metadata for core persisted models.
ALTER TABLE buses
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE routes
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE scheduled_trips
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE assignments
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE drivers
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

ALTER TABLE conductors
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

-- The old ActionLog table duplicated Envers history and is intentionally retired.
DROP TABLE IF EXISTS action_logs;
DROP TABLE IF EXISTS rev_modified_entities;

-- Only Bus retains full revision history. The existing custom revision table from V1
-- is retained because it uses identity revision numbers and records the authenticated actor.
CREATE TABLE buses_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES custom_revision_entity(id),
    revtype SMALLINT,
    registration_number VARCHAR(15),
    make VARCHAR(50),
    model VARCHAR(50),
    year_of_manufacture INTEGER,
    fuel_type VARCHAR(20),
    is_active BOOLEAN,
    seating_capacity INTEGER,
    standing_capacity INTEGER,
    ntc_permit_number BIGINT,
    comfort_type VARCHAR(20),
    is_ac BOOLEAN,
    service_type VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id, rev)
);
CREATE INDEX idx_buses_aud_rev ON buses_aud (rev);
