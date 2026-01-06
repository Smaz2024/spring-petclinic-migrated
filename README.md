# Spring Petclinic - Modernized Migration

[![Build Status](https://img.shields.io/jenkins/s/https/jenkins.example.com/petclinic.svg)](https://jenkins.example.com/job/petclinic)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Overview

This project represents a comprehensive modernization of the classic Spring Petclinic application. It has been migrated from a legacy stack (Spring 4, Java 8, Bootstrap 2) to a modern, enterprise-grade architecture suitable for financial domain standards.

## Key Features

- **Modern Stack**: Java 17 LTS, Spring Framework 6.1.x (Pure Spring), PostgreSQL 15.
- **Enhanced UI**: Upgraded to Bootstrap 5.3 with a responsive, mobile-first design.
- **Observability**: Integrated Micrometer with OpenTelemetry Bridge for distributed tracing (Trace/Span IDs) and Prometheus metrics.
- **Resilience**: Implements Circuit Breaker, Rate Limiter, and Bulkhead patterns using Resilience4J.
- **Security**: Security best practices including CSP, XSS protection, and strict content headers. No authentication required (public demo).
- **Compliance**: Audit logging for all critical operations and PII data masking.
- **Performance**: HikariCP connection pooling and Caffeine caching.
- **CI/CD**: Jenkins declarative pipeline with automated code formatting (Spotless) and testing.

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 (LTS) |
| Framework | Spring Framework | 6.1.1 |
| Database | PostgreSQL | 15.x |
| ORM | Hibernate | 6.4.1.Final |
| UI Framework | Bootstrap | 5.3.2 |
| Server | Wildfly | 30+ |
| Build Tool | Maven | 3.8+ |
| Observability | Micrometer + OTEL | 1.12.0 |
| Resilience | Resilience4J | 2.1.0 |
| Testing | JUnit 5 + Mockito | 5.10.1 |

## Getting Started

### Prerequisites

- Java 17 SDK
- Maven 3.8+
- PostgreSQL 15
- Wildfly 20+ (for deployment)

### Database Setup

1. Create a PostgreSQL database named `petclinic`.
2. Configure connection details in `src/main/resources/application.properties`.
3. The application will automatically initialize the schema using `initDB.sql` and `populateDB.sql`.

### Build & Run

```bash
# Compile and package the application
mvn clean package

# Run with Cargo (if configured) or deploy WAR to Wildfly
cp target/petclinic.war $WILDFLY_HOME/standalone/deployments/
```

### Code Formatting

This project uses **Spotless** to enforce Google Java Format.

```bash
# Check formatting
mvn spotless:check

# Apply formatting automatically
mvn spotless:apply
```


## Documentation

All documentation is consolidated in the `docs/` directory:

- [PROJECT_SUMMARY.md](docs/PROJECT_SUMMARY.md): Master summary of migration, architecture, compliance, and planning.
- [MIGRATION_SUMMARY.md](docs/MIGRATION_SUMMARY.md): Before/after migration comparison.
- [HLD.md](docs/HLD.md): High-level architecture and system design.
- [LLD.md](docs/LLD.md): Low-level design, class diagrams, and sequence flows.
- [ERD.md](docs/ERD.md): PostgreSQL database schema and relationships.
- [OBSERVABILITY_GUIDE.md](docs/OBSERVABILITY_GUIDE.md): Prometheus, Grafana, and tracing setup.
- [WILDFLY_DEPLOYMENT.md](docs/WILDFLY_DEPLOYMENT.md): WildFly deployment and optimization guide.

## Deployment

The project includes a `Jenkinsfile` for automated CI/CD. The pipeline performs:
1. Compilation
2. Unit & Integration Tests
3. Code Formatting Check
4. WAR Packaging
5. Deployment to Wildfly Environment

## License

Refactored from the original Spring Petclinic (Apache 2.0 License).
# Spring Petclinic Migration - Consolidated Documentation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Key Features](#key-features)
5. [Deployment](#deployment)
6. [Observability](#observability)
7. [Security & Compliance](#security--compliance)
8. [Development Guide](#development-guide)

---

## Overview

This project represents a comprehensive modernization of the classic Spring Petclinic application from a legacy stack (Spring 4, Java 8, Bootstrap 2) to a modern, enterprise-grade architecture.

**Project Timeline**: Started 2025-12-27 | Status: ✅ Core Migration Complete

### Why This Migration?
- **Legacy Stack**: Spring 4.x (EOL), Java 8 (outdated), HSQLDB (unsuitable for production), Bootstrap 2 (unmaintained)
- **Target**: Java 17 LTS, Spring 5.3.x (latest stable non-Boot), PostgreSQL 15, Bootstrap 5, WildFly 30+
- **Value**: Modern observability, resilience patterns, security best practices, cloud-ready architecture

---

## Technology Stack

| Component | Legacy | Modern | Rationale |
|-----------|--------|--------|-----------|
| **Java** | 8 | **17 LTS** | 10-year LTS, modern language features (records, sealed classes, pattern matching) |
| **Spring** | 4.x | **5.3.x (Pure Spring)** | Latest stable pre-Boot, full control over configuration |
| **Database** | HSQLDB/MySQL | **PostgreSQL 15** | Production-ready, excellent performance, JSON support |
| **ORM** | Hibernate 4.x | **Hibernate 6.2.13** | Full text search, better N+1 prevention, Jakarta EE |
| **UI** | Bootstrap 2.3.2 | **Bootstrap 5.3** | Modern responsive design, accessibility, mobile-first |
| **Caching** | EHCache | **Caffeine JCache** | Superior performance, better Spring integration |
| **Pool** | Tomcat JDBC | **HikariCP 6.3.3** | Industry standard, best-in-class performance |
| **Observability** | None | **Micrometer + OTEL** | Distributed tracing (trace/span IDs), Prometheus metrics |
| **Resilience** | None | **Resilience4J** | Circuit breaker, rate limiter, bulkhead patterns |
| **Testing** | JUnit 4 | **JUnit 5 + Mockito** | Modern testing framework with better extensibility |
| **Server** | JBOSS EAP 7 | **WildFly 30+** | Jakarta EE 10 support, better performance, active community |
| **Build** | Maven (basic) | **Maven + Spotless** | Automated code formatting, consistent style |

### Key Dependencies Removed (Intentional)
- ❌ Dandelion DataTables (legacy UI library)
- ❌ Joda-Time (replaced by java.time)
- ❌ Multiple repository implementations (JDBC, JPA variants)
- ❌ Legacy EHCache (replaced by Caffeine)

---

## Architecture

### Layered Architecture with AOP

```
┌─────────────────────────────────────────┐
│         Web Layer (Spring MVC)          │
│  Controllers + JSP Views (Bootstrap 5)  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Service Layer                   │
│  Business Logic + Transactions          │
│  ┌──────────────────────────────────┐  │
│  │  AOP Aspects:                    │  │
│  │  • AuditLoggingAspect            │  │
│  │  • MetricsAspect                 │  │
│  │  • LoggingAspect                 │  │
│  └──────────────────────────────────┘  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│    Repository Layer (Spring Data JPA)   │
│  OwnerRepository, PetRepository, etc.   │
└────────────────┬────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
┌───────▼──────┐  ┌───────▼────────┐
│Caffeine Cache│  │ PostgreSQL 15  │
│ (L1 Cache)   │  │  (Persistence) │
└──────────────┘  └────────────────┘
```

### Configuration Structure (Java-Based)

```
AppConfig (Root Configuration)
├── @ComponentScan (explicit base packages)
├── @EnableTransactionManagement
├── @EnableAspectJAutoProxy
└── @Import({
    ├── DataSourceConfig      → HikariCP + JNDI
    ├── JpaConfig             → Hibernate 6 + JPA
    ├── WebMvcConfig          → Controllers, formatters, interceptors
    ├── CacheConfig           → Caffeine cache setup
    ├── SecurityConfig        → Security headers, RBAC
    ├── ObservabilityConfig   → Micrometer + OTEL
    └── ResilienceConfig      → Circuit breaker, rate limiter
})
```

---

## Key Features

### ✅ Core Functionality (100% Migrated)
- **Owner Management**: Create, read, update, delete owners with addresses
- **Pet Management**: Associate pets with owners, track pet types
- **Visit Management**: Schedule and track veterinary visits
- **Vet Directory**: Browse available veterinarians and specialties

### ✨ Enhanced Features (NEW)

#### 1. **Observability Stack**
- **Metrics**: Prometheus-compatible endpoint at `/prometheus`
  - Request counts, latencies (p50/p99)
  - Custom business metrics (owners_created, visits_scheduled)
  - JVM metrics (memory, GC, threads)
  
- **Distributed Tracing**: OpenTelemetry bridge
  - Automatic trace/span ID injection via MDC
  - Correlation IDs visible in logs
  - Jaeger-compatible exports
  
- **Structured Logging**
  - JSON format with timestamps and trace IDs
  - Separate log files: `petclinic.log`, `metrics.log`, `traces.log`
  - 30-day retention with daily rotation

#### 2. **Audit Logging**
- Automatic capture of all data modifications (CREATE, UPDATE)
- Tracks: entity type, ID, user, action, timestamp, trace ID
- Complies with GDPR Article 5 (accountability principle)

#### 3. **Security Enhancements**
- **Content Security Policy (CSP)**: `script-src 'self'; style-src 'self' 'unsafe-inline'`
- **XSS Protection**: InputSanitizer utility for user inputs
- **PII Data Masking**: Telephone and address masked in logs
- **Security Headers**: X-Frame-Options, HSTS, X-Content-Type-Options
- **Session Security**: HttpOnly, Secure, SameSite cookies

#### 4. **Resilience Patterns** (Resilience4J)
- **Circuit Breaker**: Prevents cascading failures in external calls
- **Rate Limiter**: Protects against sudden traffic spikes
- **Bulkhead**: Isolates threads for different operations
- **Retry Policy**: Exponential backoff on transient failures

#### 5. **Performance Optimizations**
- **HikariCP**: Maximum pool size 10, minimum idle 2, 30-second timeout
- **Caffeine Cache**: 10-minute TTL for vets/specialties/petTypes
- **JPA N+1 Prevention**: JOIN FETCH in custom queries
- **Index Strategy**: Composite indexes on frequently filtered columns

#### 6. **Bootstrap 5 UI**
- Responsive grid system (12 columns, mobile-first)
- Bootstrap Icons integration
- Modern form styling with accessibility support
- Dark mode ready (CSS variables)

---

## Deployment

### Prerequisites
- **Java**: OpenJDK 17 LTS or later
- **Database**: PostgreSQL 15.x
- **Server**: WildFly 30.0.1+ (or JBoss EAP 7.4+)
- **Build**: Maven 3.8+

### Database Setup

```bash
# Create PostgreSQL database
createdb petclinic

# Initialize schema (scripts run automatically on startup)
# - src/main/resources/db/postgresql/initDB.sql
# - src/main/resources/db/postgresql/populateDB.sql
```

### Build & Deploy

```bash
# Build WAR package
mvn clean package

# Deploy to WildFly
cp target/petclinic.war $WILDFLY_HOME/standalone/deployments/

# Or use Maven plugin
mvn wildfly:deploy
```

### Configuration Files
- **Application**: `src/main/resources/application.properties`
- **Logging**: `src/main/resources/logback.xml` (includes trace ID injection)
- **Cache**: `src/main/resources/caffeine-jcache.properties`
- **Web**: `src/main/webapp/WEB-INF/web.xml` (Jakarta EE 6.0)

### WildFly JNDI Configuration
Datasource configured at: `java:jboss/datasources/PetclinicDS`
- Connection pool: HikariCP-managed
- Max pool: 10 connections
- Min idle: 2 connections
- Timeout: 30 seconds

---

## Observability

### Metrics Export

**Prometheus Endpoint**: `http://localhost:8080/prometheus`

Key metrics tracked:
```
# Application metrics
petclinic_owners_created_total
petclinic_visits_scheduled_total
petclinic_api_request_duration_seconds{endpoint="/owners",method="GET"}

# JVM metrics
jvm_memory_used_bytes{area="heap"}
jvm_threads_peak_threads
jvm_gc_pause_seconds{action="end of major GC"}

# HTTP metrics
http_server_requests_seconds{method="GET",uri="/owners"}
http_requests_received_total
```

### Structured Logging

**Log Format**: `timestamp | [thread] | [traceId,spanId] | level | logger | message`

**Example**:
```
2025-12-27 14:32:15.487 [http-8080-2] [550e8400-e29b-41d4-a716-446655440000,d9aaf4d3-6c6b-4def-9aef-e1f4a8a1f5f3] INFO org.springframework.samples.petclinic.service.ClinicService - Owner created: ID=123, Name=John Doe
```

**Log Levels by Component**:
- `petclinic.*`: DEBUG (detailed business logic)
- `spring.framework`: INFO (framework events)
- `hibernate`: WARN (only issues)
- `io.micrometer`: INFO (metrics only)
- `io.opentelemetry`: INFO (tracing only)

### Tracing Integration

**OpenTelemetry Bridge** with Micrometer:
- Automatic trace context propagation
- B3 propagator support for Zipkin/Jaeger
- Correlation IDs visible across all logs
- Zero-code tracing for database queries

---

## Security & Compliance

### GDPR Compliance

| Principle | Implementation |
|-----------|----------------|
| **Data Minimization** | Only collect: name, address, telephone, pet info |
| **Right to Access** | Audit logs trackable by user |
| **Right to Erasure** | Owner deletion cascades to pets/visits |
| **Data Protection** | PII masked in logs (telephone, address) |
| **Accountability** | Audit trail (7-year retention) |

### Data Masking Strategy

```java
// Original: (555) 123-4567
// Masked:   (555) ***-4567

// Original: 123 Main Street, Springfield, IL 62701
// Masked:   123 Main St, ***
```

### RBAC (Role-Based Access Control)

Two roles defined in SecurityConfig:
- **ROLE_USER**: Read-only access to owners, pets, vets, visits
- **ROLE_ADMIN**: Full CRUD on all entities

Default credentials (demo):
- User: `user` / Password: `password` (ROLE_USER)
- Admin: `admin` / Password: `password` (ROLE_ADMIN)

**Note**: This is a demo application. For production, integrate with enterprise auth (LDAP, OAuth2, OIDC).

---

## Development Guide

### Running Locally

```bash
# Build and run (requires PostgreSQL running)
mvn clean install
mvn spring-boot:run

# Or deploy to local WildFly
# Point browser to: http://localhost:8080/petclinic
```

### Code Formatting

```bash
# Check formatting
mvn spotless:check

# Apply formatting (Google Java Format)
mvn spotless:apply
```

### Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=OwnerControllerTests

# With coverage
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

### Logging & Tracing

Enable trace-level logging in `logback.xml`:
```xml
<logger name="org.springframework.samples.petclinic" level="TRACE" />
<logger name="io.opentelemetry" level="DEBUG" />
```

View logs with trace IDs:
```bash
tail -f logs/petclinic.log | grep "traceId"
tail -f logs/metrics.log
tail -f logs/traces.log
```

### Adding New Features

1. **Create domain model** → `src/main/java/org/springframework/samples/petclinic/model/`
2. **Add JPA entity** → Use Jakarta Persistence annotations
3. **Create repository** → Extend `JpaRepository<Entity, Long>`
4. **Implement service** → Add @Transactional, business logic
5. **Create controller** → Use @GetMapping, @PostMapping
6. **Build JSP view** → Use Bootstrap 5 classes
7. **Write tests** → Unit + integration tests (target 95%+ coverage)
8. **Update docs** → Document in README or feature guide

### Database Migrations

Using raw SQL migrations (Flyway-style):
1. Create script in `src/main/resources/db/postgresql/`
2. Follow naming: `V001__Initial_Schema.sql`
3. Scripts auto-applied on startup via `initDB.sql`

---

## Troubleshooting

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| `Connection refused` on startup | PostgreSQL not running | Start PostgreSQL service |
| `Bean not found` error | Missing @Component/@Service | Add annotation to class |
| `JSP not rendering` | Incorrect view resolver | Check `WebMvcConfig.configureViewResolvers()` |
| `Trace ID not in logs` | MDC not configured | Verify logback.xml includes `%X{traceId}` |
| `Metrics endpoint 404` | Prometheus exporter not registered | Check `ObservabilityConfig.prometheusRegistry()` |

### Performance Tuning

- **Slow database**: Check indexes in PostgreSQL, verify connection pool settings
- **Memory leak**: Monitor JVM metrics in Prometheus, check for unbounded caches
- **High latency**: Enable query logging in Hibernate, use JPA N+1 prevention
- **Failed deployments**: Check WildFly logs in `$WILDFLY_HOME/standalone/log/server.log`

---

## Contributing

### Code Style
- Use Spotless for automatic formatting: `mvn spotless:apply`
- Follow Google Java Format conventions
- Add Javadoc to public methods and classes
- Keep methods under 50 lines (single responsibility)

### Testing Standards
- Minimum 95% code coverage (enforced by JaCoCo)
- All public methods must have unit or integration tests
- Use descriptive test names: `testShouldCreateOwnerWithValidData()`
- Mock external dependencies (database, REST calls)

### Documentation
- Update README for API changes
- Document complex logic with inline comments
- Keep this master documentation synchronized with code

---

## Resources

### Official Documentation
- [Spring 5.3 Reference](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Jakarta EE Specification](https://jakarta.ee/specifications/)
- [PostgreSQL 15 Documentation](https://www.postgresql.org/docs/15/)
- [WildFly Administration Guide](https://docs.wildfly.org/30.0/)

### Tools & Utilities
- **IDE**: IntelliJ IDEA 2024+ or Eclipse 2024+
- **Database Client**: DBeaver, pgAdmin 4
- **API Testing**: Postman, Insomnia
- **Monitoring**: Prometheus, Grafana, Jaeger


### Documentation Index
- [PROJECT_SUMMARY.md](docs/PROJECT_SUMMARY.md) – Consolidated master documentation
- [MIGRATION_SUMMARY.md](docs/MIGRATION_SUMMARY.md) – Migration comparison
- [HLD.md](docs/HLD.md) – High-level architecture
- [LLD.md](docs/LLD.md) – Low-level design
- [ERD.md](docs/ERD.md) – Database schema
- [OBSERVABILITY_GUIDE.md](docs/OBSERVABILITY_GUIDE.md) – Observability setup
- [WILDFLY_DEPLOYMENT.md](docs/WILDFLY_DEPLOYMENT.md) – WildFly deployment

---

## License

Copyright 2002-2025 the original author or authors. Licensed under Apache License 2.0.


---

**Last Updated**: 2025-12-27
**Status**: ✅ Production Ready
