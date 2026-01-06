-- =====================================================
-- Spring Petclinic PostgreSQL Production-Grade Schema
-- =====================================================
-- Partitioning Advantages:
-- 1. Performance: Queries that filter on the partition key scan only relevant partitions, improving SELECT, UPDATE, DELETE speed.
-- 2. Maintenance: Old partitions can be archived or dropped easily without affecting new data.
-- 3. Index Management: Indexes are created per partition, optimizing large table access.
-- 4. Concurrency: Smaller partitions reduce locking issues and improve parallelism.
-- 5. Compliance & Retention: Easy to implement data retention policies, e.g., keep only 7 years of audit logs.

-- Drop tables if they exist (safe order)
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS visits CASCADE;
DROP TABLE IF EXISTS pets CASCADE;
DROP TABLE IF EXISTS types CASCADE;
DROP TABLE IF EXISTS owners CASCADE;
DROP TABLE IF EXISTS vet_specialties CASCADE;
DROP TABLE IF EXISTS specialties CASCADE;
DROP TABLE IF EXISTS vets CASCADE;

-- =======================
-- Vets table
-- =======================
CREATE TABLE vets (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL
);
CREATE INDEX idx_vets_last_name ON vets(last_name);
COMMENT ON TABLE vets IS 'Veterinarians working at the clinic';

-- =======================
-- Specialties table
-- =======================
CREATE TABLE specialties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE
);
CREATE INDEX idx_specialties_name ON specialties(name);
COMMENT ON TABLE specialties IS 'Medical specialties (radiology, surgery, dentistry)';

-- =======================
-- Vet-Specialties junction table
-- =======================
CREATE TABLE vet_specialties (
    vet_id BIGINT NOT NULL,
    specialty_id BIGINT NOT NULL,
    PRIMARY KEY (vet_id, specialty_id),
    FOREIGN KEY (vet_id) REFERENCES vets(id) ON DELETE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE
);

-- =======================
-- Pet types table
-- =======================
CREATE TABLE types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE
);
CREATE INDEX idx_types_name ON types(name);
COMMENT ON TABLE types IS 'Pet types (cat, dog, bird, etc.)';

-- =======================
-- Owners table
-- =======================
CREATE TABLE owners (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(80),
    telephone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_owners_last_name ON owners(last_name);
CREATE INDEX idx_owners_city ON owners(city);
COMMENT ON TABLE owners IS 'Pet owners - PII data masked in logs';

-- =======================
-- Pets table
-- =======================
CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    birth_date DATE,
    type_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_birth_date CHECK (birth_date <= CURRENT_DATE),
    FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES types(id)
);
CREATE INDEX idx_pets_name ON pets(name);
CREATE INDEX idx_pets_owner_id ON pets(owner_id);
CREATE INDEX idx_pets_type_id ON pets(type_id);

-- =======================
-- Visits table (partitioned by year)
-- =======================
CREATE TABLE visits (
    id BIGSERIAL NOT NULL,
    pet_id BIGINT NOT NULL,
    visit_date DATE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, visit_date),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
) PARTITION BY RANGE (visit_date);

-- Partitions for visits
CREATE TABLE visits_2023 PARTITION OF visits FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');
CREATE TABLE visits_2024 PARTITION OF visits FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
CREATE TABLE visits_2025 PARTITION OF visits FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
CREATE TABLE visits_default PARTITION OF visits DEFAULT;

-- Indexes on partitions
CREATE INDEX idx_visits_pet_id_2023 ON visits_2023(pet_id);
CREATE INDEX idx_visits_date_2023 ON visits_2023(visit_date);

CREATE INDEX idx_visits_pet_id_2024 ON visits_2024(pet_id);
CREATE INDEX idx_visits_date_2024 ON visits_2024(visit_date);

CREATE INDEX idx_visits_pet_id_2025 ON visits_2025(pet_id);
CREATE INDEX idx_visits_date_2025 ON visits_2025(visit_date);

CREATE INDEX idx_visits_pet_id_default ON visits_default(pet_id);
CREATE INDEX idx_visits_date_default ON visits_default(visit_date);

-- =======================
-- Audit log table (partitioned by month)
-- =======================
CREATE TABLE audit_log (
    id BIGSERIAL NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    user_id VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_value TEXT,
    new_value TEXT,
    trace_id VARCHAR(100),
    ip_address VARCHAR(45),
    PRIMARY KEY (id, timestamp)
) PARTITION BY RANGE (timestamp);

-- Audit log partitions (2024)
DO $$
DECLARE
    m INT;
    next_year INT := 2025;
BEGIN
    FOR m IN 1..12 LOOP
        IF m < 12 THEN
            EXECUTE format(
                'CREATE TABLE audit_log_2024_%s PARTITION OF audit_log FOR VALUES FROM (''2024-%s-01'') TO (''2024-%s-01'');',
                lpad(m::text,2,'0'), lpad(m::text,2,'0'), lpad((m+1)::text,2,'0')
            );
        ELSE
            -- December partition goes to next year
            EXECUTE format(
                'CREATE TABLE audit_log_2024_%s PARTITION OF audit_log FOR VALUES FROM (''2024-12-01'') TO (''%s-01-01'');',
                lpad(m::text,2,'0'), next_year
            );
        END IF;
    END LOOP;
END;
$$;

-- Default partition
CREATE TABLE audit_log_default PARTITION OF audit_log DEFAULT;

-- Indexes on audit_log partitions
CREATE INDEX idx_audit_entity_2024_01 ON audit_log_2024_01(entity_type, entity_id);
CREATE INDEX idx_audit_timestamp_2024_01 ON audit_log_2024_01(timestamp);
CREATE INDEX idx_audit_trace_id_2024_01 ON audit_log_2024_01(trace_id);

COMMENT ON TABLE audit_log IS 'Audit trail for CRUD operations - 7 year retention';
COMMENT ON COLUMN audit_log.trace_id IS 'Distributed tracing ID from OTEL bridge';
