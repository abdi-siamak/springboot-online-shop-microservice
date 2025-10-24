package com.siamak.shop.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    private Instant occurredAt;
    private boolean sent = false;
    private Instant sentAt;

    private OutboxEvent(UUID id, String aggregateType, String aggregateId,
                        String eventType, String payload, Instant occurredAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public static OutboxEvent of (String aggregateType, String aggregateId, String eventType, String payload,
                                  Instant occurredAt) {
        return new OutboxEvent(UUID.randomUUID(), aggregateType, aggregateId, eventType, payload, occurredAt);
    }

    public void marksent(Instant when) {
        this.sent = true;
        this.sentAt = when;
    }
}
