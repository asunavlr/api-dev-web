package com.kevinmatos.inventory.events;

import com.kevinmatos.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    private final InventoryService inventory;

    public OrderCreatedConsumer(InventoryService inventory) {
        this.inventory = inventory;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".dlt")
    @KafkaListener(topics = "orders.created.v1", groupId = "inventory-service")
    public void onOrderCreated(OrderCreated event) {
        inventory.reserve(event);
        log.info("Processed OrderCreated eventId={} orderId={} sku={} quantity={}",
                event.eventId(), event.orderId(), event.sku(), event.quantity());
    }

    @DltHandler
    public void onDeadLetter(OrderCreated event) {
        log.error("Order event moved to dead-letter topic eventId={} orderId={}",
                event.eventId(), event.orderId());
    }
}
