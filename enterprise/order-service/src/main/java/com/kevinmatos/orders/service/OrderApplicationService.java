package com.kevinmatos.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinmatos.orders.api.CreateOrderRequest;
import com.kevinmatos.orders.api.OrderController.OrderAccepted;
import com.kevinmatos.orders.domain.OrderEntity;
import com.kevinmatos.orders.domain.OutboxEvent;
import com.kevinmatos.orders.events.OrderCreated;
import com.kevinmatos.orders.events.OrderEventPublisher;
import com.kevinmatos.orders.repository.OrderRepository;
import com.kevinmatos.orders.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderApplicationService {
    private final OrderRepository orders;
    private final OutboxEventRepository outbox;
    private final ObjectMapper objectMapper;

    public OrderApplicationService(
            OrderRepository orders,
            OutboxEventRepository outbox,
            ObjectMapper objectMapper) {
        this.orders = orders;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderAccepted create(CreateOrderRequest request) {
        UUID orderId = UUID.randomUUID();
        String eventId = UUID.randomUUID().toString();
        OrderEntity order = new OrderEntity(orderId, request.customerId(), request.sku(), request.quantity());
        OrderCreated event = new OrderCreated(
                eventId, 1, orderId.toString(), request.customerId(),
                request.sku(), request.quantity(), Instant.now());
        try {
            String payload = objectMapper.writeValueAsString(event);
            orders.save(order);
            outbox.save(new OutboxEvent(
                    eventId, orderId.toString(), OrderEventPublisher.TOPIC, payload));
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not serialize order event", exception);
        }
        return new OrderAccepted(orderId.toString(), "ACCEPTED", "Order stored and queued for publication");
    }
}
