WITH superadmin_user AS (
    INSERT INTO app_users (name, email, password_hash, status)
    VALUES (
        'Alpha Super Admin',
        'alphasuperadmin@gmail.com',
        '$2a$10$3JtAdZ8/l6PY1buw8o3df.2TIiJlzBhfl8U5/k/gHt0cNRdi7D5Ui',
        'ACTIVE'
    )
    ON CONFLICT (email) DO UPDATE
        SET password_hash = EXCLUDED.password_hash,
            status = EXCLUDED.status,
            updated_at = CURRENT_TIMESTAMP
    RETURNING id
),
superadmin_role AS (
    SELECT id
    FROM app_roles
    WHERE name = 'SUPER_ADMIN'
)
INSERT INTO app_users_roles (user_id, role_id)
SELECT superadmin_user.id, superadmin_role.id
FROM superadmin_user
CROSS JOIN superadmin_role
ON CONFLICT (user_id) DO UPDATE
    SET role_id = EXCLUDED.role_id;
