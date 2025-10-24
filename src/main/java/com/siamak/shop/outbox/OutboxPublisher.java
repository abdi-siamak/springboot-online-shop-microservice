package com.siamak.shop.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxRepository repo;
    private final KafkaTemplate<String, Object> kafka;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishUnsent() throws Exception {
        //log.info("OutboxPublisher running scheduled task...");
        var batch = repo.findTop100BySentFalseOrderByOccurredAtAsc();
        log.info("Found {} unsent events", batch.size());

        for (var e : batch) {
            // topic = eventType
            Map payload = mapper.readValue(e.getPayload(), Map.class);
            kafka.send(e.getEventType(), e.getAggregateId(), payload).get(); // wait for ack
            e.marksent(Instant.now());
            log.info("Sent event {} to topic {}", e.getId(), e.getEventType());
        }
    }
}
