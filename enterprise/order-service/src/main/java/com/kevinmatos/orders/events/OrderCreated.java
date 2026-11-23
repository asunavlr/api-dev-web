package com.kevinmatos.orders.events;
import java.time.Instant;
public record OrderCreated(String eventId, int schemaVersion, String orderId, String customerId, String sku, int quantity, Instant occurredAt) {}
