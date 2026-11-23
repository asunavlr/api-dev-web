package com.kevinmatos.orders.api;
import com.kevinmatos.orders.events.OrderCreated;
import com.kevinmatos.orders.events.OrderEventPublisher;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderEventPublisher publisher;
  public OrderController(OrderEventPublisher publisher) { this.publisher = publisher; }
  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public OrderAccepted create(@Valid @RequestBody CreateOrderRequest request) {
    String orderId = UUID.randomUUID().toString();
    publisher.publish(new OrderCreated(UUID.randomUUID().toString(), 1, orderId, request.customerId(), request.sku(), request.quantity(), Instant.now()));
    return new OrderAccepted(orderId, "ACCEPTED", "Order event queued");
  }
  public record OrderAccepted(String orderId, String status, String message) {}
}
