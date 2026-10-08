SET search_path TO ptms, public;

ALTER TABLE users ADD COLUMN auth_version BIGINT NOT NULL DEFAULT 0;
