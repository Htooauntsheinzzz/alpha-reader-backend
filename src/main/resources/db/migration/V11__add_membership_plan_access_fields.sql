ALTER TABLE membership_plans
    ALTER COLUMN duration DROP NOT NULL,
    ADD COLUMN duration_unit SMALLINT,
    ADD COLUMN access_level SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN is_lifetime SMALLINT NOT NULL DEFAULT 0;

UPDATE membership_plans
SET duration_unit = 100
WHERE duration IS NOT NULL;

ALTER TABLE membership_plans
    ADD CONSTRAINT chk_membership_plans_duration_unit
        CHECK (duration_unit IS NULL OR duration_unit IN (100, 200, 300)),
    ADD CONSTRAINT chk_membership_plans_access_level
        CHECK (access_level >= 0),
    ADD CONSTRAINT chk_membership_plans_is_lifetime
        CHECK (is_lifetime IN (0, 1)),
    ADD CONSTRAINT chk_membership_plans_duration_configuration
        CHECK (
            (is_lifetime = 1 AND duration IS NULL AND duration_unit IS NULL)
            OR
            (
                is_lifetime = 0
                AND duration > 0
                AND duration_unit IN (100, 200, 300)
            )
        );
