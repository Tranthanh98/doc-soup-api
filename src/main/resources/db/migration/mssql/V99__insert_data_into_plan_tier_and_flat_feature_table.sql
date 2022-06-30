INSERT INTO plan_tier (level, name, initial_fee, initial_seat, seat_price, created_date, created_by)
VALUES (0, 'Limited Trial', 0, 0, 0, GETDATE(), NEWID())

INSERT INTO plan_tier (level, name, initial_fee, initial_seat, seat_price, created_date, created_by)
VALUES (1, 'Personal', 0, 0, 15, GETDATE(), NEWID())

INSERT INTO plan_tier (level, name, initial_fee, initial_seat, seat_price, created_date, created_by)
VALUES (2, 'Standard', 0, 0, 65, GETDATE(), NEWID())

INSERT INTO plan_tier (level, name, initial_fee, initial_seat, seat_price, created_date, created_by)
VALUES (3, 'Advanced', 250, 3, 90, GETDATE(), NEWID())

DECLARE @LimitedTrialTierId AS bigint = (Select id from plan_tier where [level]=0)
DECLARE @PersonalTierId AS bigint = (Select id from plan_tier where [level]=1)
DECLARE @StandardTierId AS bigint = (Select id from plan_tier where [level]=2)
DECLARE @AdvancedTierId AS bigint = (Select id from plan_tier where [level]=3)

-- DocumentLimit
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentLimit', @LimitedTrialTierId, 5, null, GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentLimit', @PersonalTierId, 0, null, GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentLimit', @StandardTierId, 0, null, GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentLimit', @AdvancedTierId, 0, null, GETDATE(), NEWID())

-- DocumentPages
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentPages', @LimitedTrialTierId, 500, '**', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentPages', @PersonalTierId, 500, '**', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentPages', @StandardTierId, 500, '**', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('DocumentPages', @AdvancedTierId, 500, '**', GETDATE(), NEWID())

-- UploadSizeLimit
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @LimitedTrialTierId, 2, 'GB', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @PersonalTierId, 2, 'GB', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @StandardTierId, 2, 'GB', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @AdvancedTierId, 2, 'GB', GETDATE(), NEWID())

-- IncludeStorage
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('IncludeStorage', @LimitedTrialTierId, 10, 'GB/user', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('IncludeStorage', @PersonalTierId, 10, 'GB/user', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @StandardTierId, 50, 'GB/user', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('UploadSizeLimit', @AdvancedTierId, 50, 'GB/user', GETDATE(), NEWID())

-- TotalAssetsInSpace
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('TotalAssetsInSpace', @LimitedTrialTierId, -1, 'total', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('TotalAssetsInSpace', @PersonalTierId, -1, 'total', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('TotalAssetsInSpace', @StandardTierId, 200, 'total', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('TotalAssetsInSpace', @AdvancedTierId, 2000, 'total', GETDATE(), NEWID())

-- AssetsInSpaceFolder
INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('AssetsInSpaceFolder', @LimitedTrialTierId, -1, 'per folder', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('AssetsInSpaceFolder', @PersonalTierId, -1, 'per folder', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('AssetsInSpaceFolder', @StandardTierId, -1, 'per folder', GETDATE(), NEWID())

INSERT INTO feature_flag (feature_key, plan_tier_id, limit, unit, created_date, created_by)
VALUES ('AssetsInSpaceFolder', @AdvancedTierId, 200, 'per folder', GETDATE(), NEWID())

UPDATE company
SET plan_tier_id = @LimitedTrialTierId

ALTER TABLE company
    ALTER COLUMN plan_tier_id bigint NOT NULL
GO