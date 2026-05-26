-- Enable pgvector extension for future AI embeddings
CREATE EXTENSION IF NOT EXISTS vector;

-- User Account
CREATE TABLE user_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Environment (groups habitats)
CREATE TABLE environment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(owner_id, name)
);

-- Habitat
CREATE TABLE habitat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    environment_id UUID REFERENCES environment(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_refuge BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(owner_id, name)
);

-- Object Species
CREATE TABLE object_species (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    primary_habitat_id UUID NOT NULL REFERENCES habitat(id) ON DELETE RESTRICT,
    refuge_habitat_id UUID REFERENCES habitat(id) ON DELETE SET NULL,
    environment_id UUID NOT NULL REFERENCES environment(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    size_cm DECIMAL(10, 2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(owner_id, name)
);

-- Object State (enum: UNKNOWN, FOUND, MISSING, RELOCATED)
CREATE TABLE object_state_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_species_id UUID NOT NULL REFERENCES object_species(id) ON DELETE CASCADE,
    state VARCHAR(50) NOT NULL CHECK (state IN ('UNKNOWN', 'FOUND', 'MISSING', 'RELOCATED')),
    recorded_by_id UUID NOT NULL REFERENCES user_account(id) ON DELETE RESTRICT,
    recorded_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_state_transition CHECK (state IN ('UNKNOWN', 'FOUND', 'MISSING', 'RELOCATED'))
);

-- Observation (user-provided data)
CREATE TABLE observation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_species_id UUID NOT NULL REFERENCES object_species(id) ON DELETE CASCADE,
    recorder_id UUID NOT NULL REFERENCES user_account(id) ON DELETE RESTRICT,
    note TEXT NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Media Asset
CREATE TABLE media_asset (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_species_id UUID NOT NULL REFERENCES object_species(id) ON DELETE CASCADE,
    upload_by_id UUID NOT NULL REFERENCES user_account(id) ON DELETE RESTRICT,
    url VARCHAR(1024) NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for common queries
CREATE INDEX idx_object_species_owner_id ON object_species(owner_id);
CREATE INDEX idx_object_species_environment_id ON object_species(environment_id);
CREATE INDEX idx_habitat_owner_id ON habitat(owner_id);
CREATE INDEX idx_habitat_environment_id ON habitat(environment_id);
CREATE INDEX idx_environment_owner_id ON environment(owner_id);
CREATE INDEX idx_observation_object_species_id ON observation(object_species_id);
CREATE INDEX idx_observation_recorder_id ON observation(recorder_id);
CREATE INDEX idx_state_record_object_species_id ON object_state_record(object_species_id);
CREATE INDEX idx_state_record_recorded_at ON object_state_record(recorded_at DESC);
CREATE INDEX idx_media_asset_object_species_id ON media_asset(object_species_id);
