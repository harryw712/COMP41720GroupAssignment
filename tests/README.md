# Integration and Contract Tests

This directory hosts integration workflows that exercise the happy-path order lifecycle across all services.

## HTTP + Kafka Smoke Test (Manual)

1. Start the stack via `docker compose -f infra/docker-compose.yml up`.
2. Register a restaurant and menu items using `restaurant.http` requests.
3. Submit a customer order through `ordering.http` and confirm that the API returns `201`.
4. Observe Kafka topics (`orders.created`, `restaurant.status`, `orders.status`) via `kafka-console-consumer`.
5. Call the kitchen endpoint to advance the order ticket to `DISPATCHED`.
6. Verify notification records via `GET /api/notifications?orderId=<id>`.

## Postman Collection

Import `tests/postman/food-platform.postman_collection.json` into Postman to run the automated sequence.

## Contract Test Placeholder

Add Spring Cloud Contract stubs for the REST APIs and Kafka payloads as the project matures.
