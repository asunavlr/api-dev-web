package com.kevinmatos.inventory.service;

import com.kevinmatos.inventory.domain.InventoryItem;
import com.kevinmatos.inventory.domain.InventoryReservation;
import com.kevinmatos.inventory.events.OrderCreated;
import com.kevinmatos.inventory.repository.InventoryItemRepository;
import com.kevinmatos.inventory.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final InventoryItemRepository items;
    private final InventoryReservationRepository reservations;

    public InventoryService(
            InventoryItemRepository items,
            InventoryReservationRepository reservations) {
        this.items = items;
        this.reservations = reservations;
    }

    @Transactional
    public InventoryItem setStock(String sku, int quantity) {
        InventoryItem item = items.findBySkuForUpdate(sku).orElse(null);
        if (item == null) {
            item = new InventoryItem(sku, quantity);
        } else {
            item.setAvailableQuantity(quantity);
        }
        return items.save(item);
    }

    @Transactional
    public void reserve(OrderCreated event) {
        if (reservations.existsById(event.eventId())) {
            return;
        }
        InventoryItem item = items.findBySkuForUpdate(event.sku()).orElse(null);
        String status = "REJECTED";
        if (item != null && item.reserve(event.quantity())) {
            status = "RESERVED";
            items.save(item);
        }
        reservations.save(new InventoryReservation(
                event.eventId(), event.orderId(), event.sku(), event.quantity(), status));
    }
}
