package com.kevinmatos.orders.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 120)
    private String customerId;

    @Column(nullable = false, length = 120)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OrderEntity() {}

    public OrderEntity(UUID id, String customerId, String sku, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.sku = sku;
        this.quantity = quantity;
        this.status = "ACCEPTED";
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
