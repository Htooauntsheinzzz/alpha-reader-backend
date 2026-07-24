CREATE UNIQUE INDEX uq_app_genre_active_name
    ON app_genre (LOWER(name))
    WHERE is_deleted = 0;
