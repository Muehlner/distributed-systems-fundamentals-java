package com.example.inventory.messaging.consumers;

import com.example.inventory.messaging.EventEnvelope;
import com.example.inventory.messaging.EventType;
import com.example.inventory.repository.ProcessedEvent;
import com.example.inventory.repository.ProcessedEventRepository;
import com.example.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class InventoryCommandsConsumer {
    private static final Logger log = LoggerFactory.getLogger(InventoryCommandsConsumer.class);

    private final InventoryService inventory;
    private final ProcessedEventRepository processedRepo;

    @Value("${app.consumer.idempotency.enabled:true}")
    private boolean idempotencyEnabled;

    public InventoryCommandsConsumer(InventoryService inventory, ProcessedEventRepository processedRepo) {
        this.inventory = inventory;
        this.processedRepo = processedRepo;
    }

    @KafkaListener(topics = "${app.topics.inventoryCommands}", containerFactory = "kafkaListenerContainerFactory")
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
            case ReserveInventory -> {
                String orderId = (String) data.get("orderId");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

                inventory.reserve(orderId, env.correlationId, env.eventId, items);
                log.info("Handled ReserveInventory orderId={} correlationId={}", orderId, env.correlationId);
            }
            case ReleaseInventory -> {
                String orderId = (String) data.get("orderId");
                String reason = (String) data.getOrDefault("reason", "RELEASE_REQUESTED");
                inventory.release(orderId, env.correlationId, env.eventId, reason);
                log.info("Handled ReleaseInventory orderId={} correlationId={}", orderId, env.correlationId);
            }
            default -> log.debug("Ignoring inventory command type={}", env.eventType);
        }

        if (idempotencyEnabled) processedRepo.save(new ProcessedEvent(env.eventId));
    }
}
