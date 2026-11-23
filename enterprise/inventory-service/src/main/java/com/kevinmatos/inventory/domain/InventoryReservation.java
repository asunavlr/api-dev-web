package com.kevinmatos.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {
    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;

    @Column(name = "order_id", nullable = false, unique = true, length = 36)
    private String orderId;

    @Column(nullable = false, length = 120)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InventoryReservation() {}

    public InventoryReservation(String eventId, String orderId, String sku, int quantity, String status) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.sku = sku;
        this.quantity = quantity;
        this.status = status;
        this.processedAt = Instant.now();
    }

    public String getEventId() { return eventId; }
    public String getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
}
