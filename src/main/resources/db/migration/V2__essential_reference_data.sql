SET search_path TO ptms, public;

INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_OPERATIONS_MANAGER'),
    ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;