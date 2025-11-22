# ADR-001: Microservice Boundaries and Granularity

## Context

We need to support independent evolution of customer ordering, restaurant workflow, and notification channels. Team members will iterate on different concern areas in parallel, and the assessment rubric emphasizes decomposition with clear service ownership.

## Decision

Split the platform into three Spring Boot microservices:

1. Ordering Service – owns user orders, validation, and lifecycle persistence.
2. Restaurant Service – encapsulates menu management and kitchen processing.
3. Notification Service – fans out order updates to external channels.

Each service owns its database schema (PostgreSQL) to avoid shared transaction coupling.

## Consequences

**Positive:**
- Enables independent scaling and release cadence.
- Fault isolation: a failing kitchen workflow does not block new order intake.
- Clear accountability for domain models, aligning with Domain-Driven Design.

**Negative:**
- Additional infrastructure complexity (3 runtimes + 3 databases).
- Cross-service communication requires robust observability and tracing.
- Eventual consistency must be handled (orders become "ready" asynchronously).

Mitigations include Docker Compose for local orchestration, Kubernetes manifests for prod, and Kafka topics that provide reliable event flow between services.
