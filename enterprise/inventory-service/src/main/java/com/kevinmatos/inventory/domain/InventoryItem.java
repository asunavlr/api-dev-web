package com.kevinmatos.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Version
    private long version;

    protected InventoryItem() {}

    public InventoryItem(String sku, int availableQuantity) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.availableQuantity = availableQuantity;
    }

    public String getSku() { return sku; }
    public int getAvailableQuantity() { return availableQuantity; }

    public void setAvailableQuantity(int availableQuantity) {
        if (availableQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative");
        this.availableQuantity = availableQuantity;
    }

    public boolean reserve(int quantity) {
        if (quantity <= 0 || availableQuantity < quantity) return false;
        availableQuantity -= quantity;
        return true;
    }
}
