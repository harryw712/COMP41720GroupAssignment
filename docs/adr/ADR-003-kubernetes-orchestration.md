# ADR-003: Kubernetes as the Primary Orchestrator

## Context

The project must illustrate cloud-native deployment practices, including container orchestration and fault tolerance. We also need a repeatable approach for scaling services horizontally.

## Decision

Package every service as a container image and deploy to Kubernetes using Declarative manifests (Deployment, Service, ConfigMap, Secret). Docker Compose remains for quick local feedback, but Kubernetes is the reference environment for resilience demonstrations.

## Consequences

**Positive:**
- Rolling updates, self-healing pods, and replica scaling are handled by the control plane.
- Consistent manifests support GitOps and automated CI/CD pipelines.
- Works across major clouds, avoiding vendor lock-in.

**Negative:**
- Steeper learning curve for students unfamiliar with Kubernetes.
- Additional YAML to manage (config drift risk).
- Requires container registry + image build pipelines.

Mitigations: Provide turnkey manifests, document kubectl workflows, and keep Compose parity to ease onboarding before moving to Kubernetes.
