CREATE INDEX idx_app_genre_is_deleted_id
    ON app_genre (is_deleted, id);

CREATE INDEX idx_app_genre_name_is_deleted
    ON app_genre (name, is_deleted);
