package com.kevinmatos.orders.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(name = "aggregate_id", nullable = false, length = 36)
    private String aggregateId;

    @Column(nullable = false, length = 120)
    private String topic;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    protected OutboxEvent() {}

    public OutboxEvent(String eventId, String aggregateId, String topic, String payload) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.topic = topic;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.attempts = 0;
    }

    public UUID getId() { return id; }
    public String getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public int getAttempts() { return attempts; }
    public String getLastError() { return lastError; }

    public void markPublished() {
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(Exception exception) {
        this.attempts++;
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        this.lastError = message.substring(0, Math.min(message.length(), 1000));
    }
}
