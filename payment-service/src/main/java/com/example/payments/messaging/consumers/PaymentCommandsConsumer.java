package com.example.payments.messaging.consumers;

import com.example.payments.messaging.EventEnvelope;
import com.example.payments.messaging.EventType;
import com.example.payments.repository.ProcessedEvent;
import com.example.payments.repository.ProcessedEventRepository;
import com.example.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class PaymentCommandsConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentCommandsConsumer.class);

    private final PaymentService payments;
    private final ProcessedEventRepository processedRepo;

    @Value("${app.consumer.idempotency.enabled:true}")
    private boolean idempotencyEnabled;

    public PaymentCommandsConsumer(PaymentService payments, ProcessedEventRepository processedRepo) {
        this.payments = payments;
        this.processedRepo = processedRepo;
    }

    @KafkaListener(topics = "${app.topics.paymentsCommands}", containerFactory = "kafkaListenerContainerFactory")
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
            case AuthorizePayment -> {
                String orderId = (String) data.get("orderId");
                double amount = data.get("amount") instanceof Number n ? n.doubleValue() : 0.0;
                String currency = (String) data.getOrDefault("currency", "USD");
                payments.authorize(orderId, env.correlationId, env.eventId, amount, currency);
                log.info("Handled AuthorizePayment orderId={} correlationId={}", orderId, env.correlationId);
            }
            case RefundRequested -> {
                String orderId = (String) data.get("orderId");
                String reason = (String) data.getOrDefault("reason", "REFUND_REQUESTED");
                payments.refund(orderId, env.correlationId, env.eventId, reason);
                log.info("Handled RefundRequested orderId={} correlationId={}", orderId, env.correlationId);
            }
            default -> log.debug("Ignoring payment command type={}", env.eventType);
        }

        if (idempotencyEnabled) processedRepo.save(new ProcessedEvent(env.eventId));
    }
}
