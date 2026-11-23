# Event-driven commerce services

A Java 21 / Spring Boot 3 reference system demonstrating reliable asynchronous order processing with Kafka.

## Architecture

~~~text
Client
  |
  | POST /api/orders
  v
Order Service (8081) ---- PostgreSQL
  |                          |
  | transaction              | orders + outbox_events
  +--------------------------+
  |
  | scheduled outbox publisher (broker acknowledgement)
  v
Kafka topic: orders.created.v1
  |
  v
Inventory Service (8082) ---- PostgreSQL
  |                              |
  | retry topics + DLT            | inventory_items + inventory_reservations
  +-- idempotent reservation -----+
~~~

## Reliability patterns

- **Transactional outbox:** order persistence and event persistence commit in one database transaction. A scheduled publisher sends pending events and marks them published only after Kafka acknowledges them.
- **At-least-once delivery:** a process crash after broker acknowledgement but before the outbox commit can publish a duplicate. The inventory consumer deduplicates by immutable eventId.
- **Retry and dead-letter:** transient listener failures are retried with exponential backoff; exhausted events are routed to a dead-letter topic with a visible handler.
- **Concurrency control:** inventory updates use pessimistic row locking and an optimistic version column.
- **Business outcome:** inventory reservations are marked RESERVED or REJECTED based on available stock.

## Run locally

Requirements: Docker and Docker Compose.

~~~bash
docker compose -f enterprise/docker-compose.yml up --build
~~~

Services:
- Order API: http://localhost:8081
- Inventory API: http://localhost:8082
- Redpanda Kafka external listener: localhost:19092
- Orders PostgreSQL: localhost:5433
- Inventory PostgreSQL: localhost:5434

## Walkthrough

1. Add stock:

~~~bash
curl -X PUT http://localhost:8082/api/inventory/items \
  -H "Content-Type: application/json" \
  -d '{"sku":"SKU-100","availableQuantity":10}'
~~~

2. Create an order:

~~~bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","sku":"SKU-100","quantity":2}'
~~~

3. Read the reservation using the returned orderId:

~~~bash
curl http://localhost:8082/api/inventory/reservations/ORDER_ID
curl http://localhost:8082/api/inventory/items/SKU-100
~~~

The first request returns HTTP 202 after the order and outbox event are committed. The reservation is asynchronous; poll the reservation endpoint until it appears.

## API endpoints

| Service | Method | Endpoint | Purpose |
|---|---|---|---|
| Orders | POST | /api/orders | Accept order and persist outbox event |
| Inventory | PUT | /api/inventory/items | Set available stock for an SKU |
| Inventory | GET | /api/inventory/items/{sku} | Read stock |
| Inventory | GET | /api/inventory/reservations/{orderId} | Read reservation outcome |

Both services expose Spring Boot Actuator health, info, metrics and Prometheus endpoints.

## Tests

~~~bash
cd enterprise/order-service && mvn verify
cd ../inventory-service && mvn verify
~~~

The test profile uses H2 and does not require a running Kafka broker. The CI workflow builds and tests each service independently.

## Production considerations

This is a portfolio reference implementation, not a turnkey production commerce platform. Before production, add authentication/authorization, rate limits, API idempotency keys, schema compatibility checks (e.g. Schema Registry), outbox retention/cleanup, distributed publisher locking or SKIP LOCKED, consumer lag alerts, reconciliation for rejected reservations, secrets management, TLS, backups and load/failure testing. The local Compose credentials are intentionally disposable and must never be reused in a shared environment.
