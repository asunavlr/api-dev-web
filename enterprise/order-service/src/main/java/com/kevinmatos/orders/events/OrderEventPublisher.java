package com.kevinmatos.orders.events;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class OrderEventPublisher {
  public static final String TOPIC = "orders.created.v1";
  private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
  public OrderEventPublisher(KafkaTemplate<String, OrderCreated> kafkaTemplate) { this.kafkaTemplate = kafkaTemplate; }
  public void publish(OrderCreated event) { kafkaTemplate.send(TOPIC, event.orderId(), event); }
}
