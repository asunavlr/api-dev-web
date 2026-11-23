package com.kevinmatos.orders;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinmatos.orders.api.CreateOrderRequest;
import com.kevinmatos.orders.repository.OrderRepository;
import com.kevinmatos.orders.repository.OutboxEventRepository;
import com.kevinmatos.orders.service.OrderApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderApplicationServiceTest {
    @Autowired OrderApplicationService service;
    @Autowired OrderRepository orders;
    @Autowired OutboxEventRepository outbox;
    @Autowired ObjectMapper objectMapper;

    @Test
    void persistsOrderAndOutboxEventInOneTransaction() {
        var accepted = service.create(new CreateOrderRequest("customer-1", "SKU-42", 2));

        assertThat(accepted.status()).isEqualTo("ACCEPTED");
        assertThat(orders.findById(java.util.UUID.fromString(accepted.orderId()))).isPresent();
        assertThat(outbox.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc())
                .anySatisfy(event -> {
                    assertThat(event.getAggregateId()).isEqualTo(accepted.orderId());
                    assertThat(event.getAttempts()).isZero();
                    assertThat(event.getPublishedAt()).isNull();
                });
    }
}
