# Observability Guide

The application uses **Micrometer** to bridge application metrics to **Prometheus**, and **OpenTelemetry** for distributed tracing.

## Metrics Architecture

3. **Scraping**: Prometheus server scrapes this endpoint every 15s.
4. **Visualization**: Grafana queries Prometheus to display dashboards.

## Key Metrics

| Metric Name | Type | Description |
|-------------|------|-------------|
| `petclinic.owner.created` | Counter | Number of new owners registered |
| `petclinic.visit.created` | Counter | Number of visits scheduled |
| `http.server.requests` | Timer | Request duration and throughput |
| `resilience4j.circuitbreaker.state` | Gauge | State of circuit breakers (CLOSED, OPEN) |
| `hikaricp.connections.active` | Gauge | Active DB connections |

## Distributed Tracing

The application automatically injects `traceId` and `spanId` into:
1. **Logs**: via SLF4J MDC (Mapped Diagnostic Context).
2. **Audit Logs**: Stored in the database `audit_log` table.

### Log Format Example
```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "level": "INFO",
  "traceId": "a1b2c3d4e5f6",
  "spanId": "12345678",
  "message": "Saving owner: George Franklin",
  "logger": "org.springframework.samples.petclinic.service.ClinicServiceImpl"
}
```

## Setup Instructions

1. **Prometheus Config**: Add a scrape job targeting `host.docker.internal:8080`.
2. **Grafana**: Import the provided dashboard JSON.
3. **Verification**: Generate traffic and observe the "Requests per Second" panel.
