# Distributed Food Ordering Platform

A teaching-oriented distributed backend that showcases architectural principles from COMP41720. Three Spring Boot microservices (Ordering, Restaurant, Notification) collaborate through REST and Apache Kafka, each backed by its own PostgreSQL database. The stack is containerized with Docker and ready for Kubernetes deployment.

## Repository Layout

- `services/ordering-service` – customer- and order-facing APIs, event consumers, PostgreSQL persistence
- `services/restaurant-service` – menu and kitchen management, Kafka producers, independent data store
- `services/notification-service` – asynchronous order status fan-out with retry/backoff semantics
- `infra/` – Docker Compose stack, Kubernetes manifests, API Gateway and security placeholders
- `docs/` – architectural narrative, ADRs, implementation guide, diagrams
- `tests/` – cross-service integration and contract tests (placeholder)

## Quick Start

1. Install Java 17, Maven 3.9+, Docker, Docker Compose, and kubectl.
2. Launch infra dependencies: `docker compose -f infra/docker-compose.yml up -d`.
3. Build every service: `mvn -pl services/* clean package`.
4. Start each microservice locally (example): `mvn -pl services/ordering-service spring-boot:run`.
5. Use the workflow in `docs/implementation-guide.md` to seed sample data, place an order, observe asynchronous updates, and validate observability hooks.

## Documentation

- `docs/system-architecture.md` – system diagram, service boundaries, trade-offs, security posture
- `docs/adr/ADR-001.md`..`003` – architectural decision records with context/decision/consequences
- `docs/implementation-guide.md` – detailed environment setup, build + deploy + test instructions
- `TODO.md` – actionable backlog for incremental development

## Contributing

Pull requests should:

- maintain English naming/commenting conventions
- include automated tests whenever business logic changes
- update ADRs or system documentation when modifying architecture-affecting code

Refer to the implementation guide for verification scripts and troubleshooting tips.
