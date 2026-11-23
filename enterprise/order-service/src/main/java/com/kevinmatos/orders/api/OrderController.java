package com.kevinmatos.orders.api;

import com.kevinmatos.orders.service.OrderApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderApplicationService orders;

    public OrderController(OrderApplicationService orders) {
        this.orders = orders;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderAccepted create(@Valid @RequestBody CreateOrderRequest request) {
        return orders.create(request);
    }

    public record OrderAccepted(String orderId, String status, String message) {}
}
