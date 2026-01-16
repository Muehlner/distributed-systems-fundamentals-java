package com.example.orders.messaging.consumers;

import com.example.orders.repository.ProcessedEvent;
import com.example.orders.messaging.EventEnvelope;
import com.example.orders.messaging.EventType;

import com.example.orders.repository.ProcessedEventRepository;
import com.example.orders.service.OrderCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class InventoryEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryEventsConsumer.class);

    private final OrderCommandService commands;
    private final ProcessedEventRepository processedRepo;

    @Value("${app.consumer.idempotency.enabled:true}")
    private boolean idempotencyEnabled;

    public InventoryEventsConsumer(OrderCommandService commands, ProcessedEventRepository processedRepo) {
        this.commands = commands;
        this.processedRepo = processedRepo;
    }

    @KafkaListener(topics = "${app.topics.inventoryEvents}", containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onMessage(EventEnvelope env) {
        if (env == null || env.eventId == null || env.eventType == null) return;

        if (idempotencyEnabled && processedRepo.existsById(env.eventId)) {
            log.info("Skipping already processed eventId={} type={}", env.eventId, env.eventType);
            return;
        }

        EventType type = EventType.valueOf(env.eventType);
        Map<String, Object> data = env.data;

        switch (type) {
            case InventoryReserved -> {
                String orderId = (String) data.get("orderId");
                commands.onInventoryReserved(orderId);
                log.info("Handled InventoryReserved orderId={} correlationId={}", orderId, env.correlationId);
            }
            case InventoryReservationFailed -> {
                String orderId = (String) data.get("orderId");
                String reason = (String) data.getOrDefault("reason", "INVENTORY_FAILED");
                commands.onInventoryFailed(orderId, reason);
                log.info("Handled InventoryReservationFailed orderId={} correlationId={}", orderId, env.correlationId);
            }
            default -> log.debug("Ignoring inventory event type={}", env.eventType);
        }

        if (idempotencyEnabled) processedRepo.save(new ProcessedEvent(env.eventId));
    }
}
