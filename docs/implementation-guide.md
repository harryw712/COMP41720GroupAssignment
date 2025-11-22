# Implementation & Operations Guide

This runbook walks through every step required to build, deploy, and operate the distributed food ordering platform in both local (Docker Compose) and cluster (Kubernetes) environments.

## 1. Prerequisites

| Tool | Version | Notes |
| --- | --- | --- |
| Java | 17 (Temurin recommended) | `java -version` should report 17.x |
| Maven | 3.9+ | Used for multi-module builds |
| Docker Engine | 24+ | Needed for local containers |
| Docker Compose | v2 | Compose file syntax 3.9 |
| kubectl | 1.29+ | Optional, for Kubernetes deployment |
| Helm (optional) | 3.13+ | For installing managed Kafka/PostgreSQL charts |

## 2. Build Workflow

```bash
mvn clean verify     # runs unit tests across all services
mvn -pl services/ordering-service spring-boot:run   # start a single service for development
```

Useful Maven profiles:

- `-DskipTests` for quicker container builds
- `-pl services/<module> -am` to build a single microservice and its dependencies

## 3. Local Stack (Docker Compose)

1. `docker compose -f infra/docker-compose.yml up --build -d`
2. Verify health:
   - `curl http://localhost:8080/actuator/health`
   - `docker compose logs ordering-service -f`
3. Seed data:
   - POST `http://localhost:8081/api/restaurants` with `{ "name": "Campus Kitchen" }`
   - POST menu items under `/api/restaurants/{id}/menu`
4. Place an order via Ordering service (see `tests/postman/` collection).
5. Advance kitchen status: `POST http://localhost:8081/api/kitchen/{orderId}/status` with `{ "status": "DISPATCHED" }`.
6. Notifications appear under `http://localhost:8082/api/notifications?orderId=<uuid>`.

Shut down with `docker compose -f infra/docker-compose.yml down -v`.

## 4. Kubernetes Deployment

1. Create namespace and shared secrets:
   ```bash
   kubectl apply -f infra/k8s/platform.yaml
   kubectl apply -f infra/k8s/secrets-sample.yaml -n food-platform
   ```
2. Deploy services:
   ```bash
   kubectl apply -n food-platform -f infra/k8s/ordering-service.yaml
   kubectl apply -n food-platform -f infra/k8s/restaurant-service.yaml
   kubectl apply -n food-platform -f infra/k8s/notification-service.yaml
   kubectl apply -n food-platform -f infra/k8s/api-gateway.yaml
   ```
3. Watch rollout: `kubectl get pods -n food-platform`.
4. Port-forward for testing: `kubectl port-forward svc/api-gateway 8088:80 -n food-platform`.
5. Execute the same API flows through `http://localhost:8088` with Host header `ordering.local` (Traefik routing rule).

## 5. Observability Hooks

- **Metrics**: Spring Boot Actuator exposes `/actuator/metrics`. Hook into Prometheus via ServiceMonitor or Grafana Agent.
- **Tracing**: Add the OpenTelemetry Java agent (`-javaagent:/otel/opentelemetry-javaagent.jar`). Configure OTLP exporter via env vars.
- **Logging**: Direct logs to stdout for container aggregation. For production, ship to ELK/Datadog using sidecars or Fluent Bit.

## 6. Security Hardening Checklist

- Issue TLS certificates (Let's Encrypt, cert-manager) and enable HTTPS entrypoint in Traefik.
- Enable mTLS between services using Linkerd or Istio. Update manifests with annotations to inject sidecars.
- Rotate Kafka credentials per-service using SASL/SCRAM; update `spring.kafka.*` settings accordingly.
- Enforce JWT validation in the gateway by integrating with your IdP (Okta, Auth0, Azure AD). Replace the dummy middleware in `infra/api-gateway/traefik.yml` with a production-ready plugin or dedicated auth proxy.

## 7. Disaster Recovery & Fault Injection

- **Kafka outage**: Stop the Kafka container to ensure services back off (ordering falls back to synchronous status updates). Observe Resilience4j metrics at `/actuator/metrics/resilience4j.circuitbreaker.state`.
- **Database failover**: Restart `ordering-db` to confirm Spring Boot reconnects automatically. For Kubernetes, use StatefulSets with PersistentVolumeClaims.
- **Replay events**: Use `kafka-console-consumer` to reprocess `orders.created` events for validation.

## 8. Testing Strategy

- Unit tests live beside each service (see `src/test/java`). Extend them with business cases.
- Integration/regression flows are scripted in `tests/` (Postman + shell scripts).
- Add contract tests to prevent API drift between Ordering and Restaurant services.

## 9. Troubleshooting Cheatsheet

| Symptom | Likely Cause | Fix |
| --- | --- | --- |
| `Connection refused` from Ordering → Restaurant | Wrong `RESTAURANT_SERVICE_URL` | Update env var or ConfigMap |
| Kafka deserialization errors | Topic schema drift | Align DTOs, update `spring.kafka.consumer.properties.spring.json.trusted.packages` |
| Notifications stuck in `PENDING` | Kafka topic unavailable or DB down | Check Kafka logs, DB health, then replay events |
| Kubernetes rollout stuck | Failing readiness probe | Inspect `kubectl describe pod`, tail logs, patch env vars |

## 10. Next Steps

- Automate database migrations with Flyway/Liquibase.
- Add schema registry for Kafka payload validation.
- Implement CI pipelines (GitHub Actions) to build, test, and publish container images.
- Extend Notification adapters for real providers (SES, Twilio).
