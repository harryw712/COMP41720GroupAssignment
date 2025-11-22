# ADR-002: Event-Driven Communication via Kafka

## Context

Kitchen operations and user notifications should react to order lifecycle events without increasing synchronous coupling. The rubric requires demonstrating asynchronous messaging and trade-offs around resilience.

## Decision

Adopt Apache Kafka as the inter-service message broker. Key topics:
- `orders.created` (Ordering → Restaurant)
- `restaurant.status` (Restaurant → Ordering)
- `orders.status` (Ordering → Notification)

Publishers and consumers use JSON payloads with idempotent processing to tolerate retries.

## Consequences

**Positive:**
- Natural buffering isolating spikes in demand from kitchen capacity.
- Built-in replay lets us recover from downstream outages by reprocessing events.
- Enables future services (analytics, fraud detection) to subscribe without impacting existing APIs.

**Negative:**
- Operational overhead of running Kafka + ZooKeeper (or Kraft) clusters.
- Requires schema governance to avoid serialization errors.
- Debugging event chains is harder than tracing synchronous calls.

Mitigations: Compose/K8s manifests spin up Kafka quickly; documentation details schema evolution best practices; Resilience4j ensures producers back off when Kafka is unavailable.
