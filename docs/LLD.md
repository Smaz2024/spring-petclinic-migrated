# Low Level Design (LLD)

## Service Layer

The service layer implements the Facade pattern through `ClinicService`, which delegates to specialized Repositories.

```mermaid
classDiagram
    class ClinicService {
        <<interface>>
        +findPetById(int id) Pet
        +findOwnerById(int id) Owner
        +saveVisit(Visit visit) void
        +findVets() Collection~Vet~
    }
    
    class ClinicServiceImpl {
        -PetRepository petRepository
        -VetRepository vetRepository
        -OwnerRepository ownerRepository
        -VisitRepository visitRepository
        +findPetById(int id) Pet
        +findOwnerById(int id) Owner
        +saveVisit(Visit visit) void
    }
    
    ClinicService <|.. ClinicServiceImpl
    ClinicServiceImpl --> PetRepository
    ClinicServiceImpl --> VetRepository
    ClinicServiceImpl --> OwnerRepository
```


## Configuration Externalization

All configuration (database, cache, JPA, resilience, observability, etc.) is externalized in `src/main/resources/application.properties`. No sensitive or environment-specific values are hardcoded in Java code. See the properties file for details.

## Resilience Strategy

Circuit Breakers are applied at the Service layer to protect against database or external system failures.

```mermaid
sequenceDiagram
    participant Controller
    participant Service
    participant CircuitBreaker
    participant DB
    
    Controller->>Service: findVets()
    Service->>CircuitBreaker: executeSupplier()
    
    alt Circuit Closed (Normal)
        CircuitBreaker->>DB: findAll()
        DB-->>CircuitBreaker: List<Vet>
        CircuitBreaker-->>Service: List<Vet>
        Service-->>Controller: List<Vet>
    else Circuit Open (Failure)
        CircuitBreaker-->>Service: CallNotPermittedException
        Service-->>Controller: Fallback / Error
    end
```

## Audit Logging Flow

Audit logging is implemented using Spring AOP around transactional methods.

1. **Intercept**: `@Around` advice intercepts methods annotated with `@Transactional`.
2. **Extract**: Captures method arguments (Entity) and Principal (User).
3. **Log**: Writes an `AuditLog` entry to the database within the same transaction.

```mermaid
sequenceDiagram
    participant User
    participant Controller
    participant AuditAspect
    participant Service
    participant DB
    
    User->>Controller: POST /owners/new
    Controller->>Service: saveOwner(owner)
    AuditAspect->>AuditAspect: intercept()
    AuditAspect->>Service: proceed()
    Service->>DB: INSERT INTO owners
    Service-->>AuditAspect: return owner
    AuditAspect->>DB: INSERT INTO audit_log
    AuditAspect-->>Controller: return owner
    Controller-->>User: 302 Redirect
```
