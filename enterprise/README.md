# Event-driven commerce platform

This workspace extends the existing Spring Boot commerce API with an independently runnable event-driven order flow.

## Services
- order-service: validates orders and publishes OrderCreated events.
- inventory-service: consumes order events (demo in-memory handler).

## Run locally
```bash
docker compose -f docker-compose.kafka.yml up -d
cd enterprise/order-service && mvn spring-boot:run
# in another terminal:
cd enterprise/inventory-service && mvn spring-boot:run
```

Order API: POST http://localhost:8081/api/orders

Example body:
```json
{"customerId":"cust-100","sku":"keyboard-01","quantity":2}
```

The order service publishes to orders.created.v1; inventory consumes with its own consumer group. This is a portfolio reference, not a production inventory ledger. Production hardening: transactional outbox/inbox, database-backed stock reservation, schema registry, retries/DLQ, OAuth2, Testcontainers, tracing and SLOs.
