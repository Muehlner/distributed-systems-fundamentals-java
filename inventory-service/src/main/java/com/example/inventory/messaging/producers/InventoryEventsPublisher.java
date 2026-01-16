package com.example.inventory.messaging.producers;

import com.example.inventory.messaging.EventEnvelope;
import com.example.inventory.messaging.EventType;
import com.example.inventory.util.CorrelationIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InventoryEventsPublisher {
    private static final Logger log = LoggerFactory.getLogger(InventoryEventsPublisher.class);

    private final KafkaTemplate<String, EventEnvelope> kafka;

    @Value("${app.topics.inventoryEvents}")
    private String inventoryEvents;

    public InventoryEventsPublisher(KafkaTemplate<String, EventEnvelope> kafka) {
        this.kafka = kafka;
    }

    public void publishReserved(String orderId, String correlationId, String causationId, List<Map<String, Object>> items) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reservationId", "s-" + CorrelationIds.newId().substring(0, 8));
        data.put("items", items);

        var env = EventEnvelope.of(
                EventType.InventoryReserved,
                CorrelationIds.newId(),
                correlationId,
                causationId,
                "inventory-service",
                data
        );

        kafka.send(inventoryEvents, orderId, env);
        log.info("Published InventoryReserved orderId={} correlationId={}", orderId, correlationId);
    }

    public void publishReservationFailed(String orderId, String correlationId, String causationId, String reason) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reason", reason);

        var env = EventEnvelope.of(
                EventType.InventoryReservationFailed,
                CorrelationIds.newId(),
                correlationId,
                causationId,
                "inventory-service",
                data
        );

        kafka.send(inventoryEvents, orderId, env);
        log.info("Published InventoryReservationFailed orderId={} correlationId={} reason={}", orderId, correlationId, reason);
    }

    public void publishReleased(String orderId, String correlationId, String causationId, String reason) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reason", reason);

        var env = EventEnvelope.of(
                EventType.InventoryReleased,
                CorrelationIds.newId(),
                correlationId,
                causationId,
                "inventory-service",
                data
        );

        kafka.send(inventoryEvents, orderId, env);
        log.info("Published InventoryReleased orderId={} correlationId={}", orderId, correlationId);
    }
}
