# Entity Relationship Diagram (ERD)

The following diagram illustrates the PostgreSQL database schema for the migrated Petclinic application.

```mermaid
erDiagram
    OWNERS ||--o{ PETS : owns
    OWNERS {
        BIGSERIAL id PK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR address
        VARCHAR city
        VARCHAR telephone
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    PETS ||--o{ VISITS : has
    PETS ||--|| TYPES : is_a
    PETS {
        BIGSERIAL id PK
        VARCHAR name
        DATE birth_date
        BIGINT type_id FK
        BIGINT owner_id FK
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    TYPES {
        BIGSERIAL id PK
        VARCHAR name
    }

    VISITS {
        BIGSERIAL id PK
        BIGINT pet_id FK
        DATE visit_date
        VARCHAR description
        TIMESTAMP created_at
    }

    VETS ||--o{ VET_SPECIALTIES : has
    VETS {
        BIGSERIAL id PK
        VARCHAR first_name
        VARCHAR last_name
    }

    SPECIALTIES ||--o{ VET_SPECIALTIES : includes
    SPECIALTIES {
        BIGSERIAL id PK
        VARCHAR name
    }

    VET_SPECIALTIES {
        BIGINT vet_id PK, FK
        BIGINT specialty_id PK, FK
    }

    AUDIT_LOG {
        BIGSERIAL id PK
        VARCHAR entity_type
        BIGINT entity_id
        VARCHAR action
        VARCHAR user_id
        TIMESTAMP timestamp
        TEXT old_value
        TEXT new_value
        VARCHAR trace_id
        VARCHAR ip_address
    }
```

## Schema Improvements (vs Legacy)

1. **Scalability**: All Primary Keys upgraded to `BIGSERIAL` (64-bit integers).
2. **Partitioning**: `VISITS` and `AUDIT_LOG` tables are partitioned by date range to handle high volume.
3. **Data Integrity**: Added `ON DELETE CASCADE` constraints and `CHECK` constraints (e.g. birth_date <= current_date).
4. **Compliance**: Dedicated `AUDIT_LOG` table for tracking all sensitive CRUD operations.
