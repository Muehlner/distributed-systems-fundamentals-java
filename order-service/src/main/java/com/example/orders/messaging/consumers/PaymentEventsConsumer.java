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
public class PaymentEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventsConsumer.class);

    private final OrderCommandService commands;
    private final ProcessedEventRepository processedRepo;

    @Value("${app.consumer.idempotency.enabled:true}")
    private boolean idempotencyEnabled;

    public PaymentEventsConsumer(OrderCommandService commands, ProcessedEventRepository processedRepo) {
        this.commands = commands;
        this.processedRepo = processedRepo;
    }

    @KafkaListener(topics = "${app.topics.paymentsEvents}", containerFactory = "kafkaListenerContainerFactory")
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
            case PaymentAuthorized -> {
                String orderId = (String) data.get("orderId");
                commands.onPaymentAuthorized(orderId);
                log.info("Handled PaymentAuthorized orderId={} correlationId={}", orderId, env.correlationId);
            }
            case PaymentFailed -> {
                String orderId = (String) data.get("orderId");
                commands.onPaymentFailed(orderId);
                log.info("Handled PaymentFailed orderId={} correlationId={}", orderId, env.correlationId);
            }
            case RefundCompleted -> {
                String orderId = (String) data.get("orderId");
                commands.onRefundCompleted(orderId);
                log.info("Handled RefundCompleted orderId={} correlationId={}", orderId, env.correlationId);
            }
            case RefundFailed -> {
                // For lab: just log; you can add retries/alerts later.
                String orderId = (String) data.get("orderId");
                log.warn("RefundFailed orderId={} correlationId={}", orderId, env.correlationId);
            }
            default -> log.debug("Ignoring payment event type={}", env.eventType);
        }

        if (idempotencyEnabled) processedRepo.save(new ProcessedEvent(env.eventId));
    }
}
