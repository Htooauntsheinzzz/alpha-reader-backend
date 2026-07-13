INSERT INTO app_roles (name, description)
VALUES
    ('SUPER_ADMIN', 'Super administrator role'),
    ('ADMIN', 'Administrator role')
ON CONFLICT (name) DO NOTHING;
