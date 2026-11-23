package com.kevinmatos.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import com.kevinmatos.inventory.events.OrderCreated;
import com.kevinmatos.inventory.repository.InventoryItemRepository;
import com.kevinmatos.inventory.repository.InventoryReservationRepository;
import com.kevinmatos.inventory.service.InventoryService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceTest {
    @Autowired InventoryService inventory;
    @Autowired InventoryItemRepository items;
    @Autowired InventoryReservationRepository reservations;

    @Test
    void reservesStockOnceWhenKafkaRedeliversAnEvent() {
        inventory.setStock("SKU-100", 5);
        var event = new OrderCreated("event-1", 1, "order-1", "customer-1", "SKU-100", 2, Instant.now());

        inventory.reserve(event);
        inventory.reserve(event);

        assertThat(items.findBySku("SKU-100").orElseThrow().getAvailableQuantity()).isEqualTo(3);
        assertThat(reservations.findByOrderId("order-1").orElseThrow().getStatus()).isEqualTo("RESERVED");
    }

    @Test
    void rejectsReservationWhenStockIsInsufficient() {
        inventory.setStock("SKU-200", 1);
        var event = new OrderCreated("event-2", 1, "order-2", "customer-1", "SKU-200", 3, Instant.now());

        inventory.reserve(event);

        assertThat(items.findBySku("SKU-200").orElseThrow().getAvailableQuantity()).isEqualTo(1);
        assertThat(reservations.findByOrderId("order-2").orElseThrow().getStatus()).isEqualTo("REJECTED");
    }
}
