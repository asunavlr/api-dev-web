package com.kevinmatos.orders.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinmatos.orders.domain.OutboxEvent;
import com.kevinmatos.orders.repository.OutboxEventRepository;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxEventRepository outbox;
    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outbox,
            KafkaTemplate<String, OrderCreated> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.outbox = outbox;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:1000}")
    @Transactional
    public void publishPending() {
        for (OutboxEvent pending : outbox.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            try {
                OrderCreated event = objectMapper.readValue(pending.getPayload(), OrderCreated.class);
                kafkaTemplate.send(pending.getTopic(), pending.getAggregateId(), event)
                        .get(10, TimeUnit.SECONDS);
                pending.markPublished();
            } catch (Exception exception) {
                pending.markFailed(exception);
                log.warn("Outbox publish failed eventId={} attempts={}", pending.getEventId(), pending.getAttempts(), exception);
            }
        }
    }
}
