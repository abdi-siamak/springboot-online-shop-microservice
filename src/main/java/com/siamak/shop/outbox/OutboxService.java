package com.siamak.shop.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.Instant;
import lombok.*;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository repo;
    private final ObjectMapper mapper;

    public void add(String aggregateType, String aggregteId, String eventType, Object eventPayload) {
        try {
            String json = mapper.writeValueAsString(eventPayload);
            repo.save(OutboxEvent.of(aggregateType, aggregteId, eventType, json, Instant.now()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write outbox event", e);
        }
    }
}
