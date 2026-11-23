package com.kevinmatos.inventory.events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class OrderCreatedConsumer {
  private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);
  @KafkaListener(topics = "orders.created.v1", groupId = "inventory-service")
  public void onOrderCreated(OrderCreated event) {
    log.info("Received OrderCreated eventId={} orderId={} sku={} quantity={}", event.eventId(), event.orderId(), event.sku(), event.quantity());
  }
}
