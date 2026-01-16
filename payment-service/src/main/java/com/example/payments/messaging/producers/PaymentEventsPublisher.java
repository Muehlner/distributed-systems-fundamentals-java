package com.example.payments.messaging.producers;

import com.example.payments.messaging.EventEnvelope;
import com.example.payments.messaging.EventType;
import com.example.payments.util.CorrelationIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentEventsPublisher {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventsPublisher.class);

    private final KafkaTemplate<String, EventEnvelope> kafka;

    @Value("${app.topics.paymentsEvents}")
    private String paymentsEvents;

    public PaymentEventsPublisher(KafkaTemplate<String, EventEnvelope> kafka) {
        this.kafka = kafka;
    }

    public void publishPaymentAuthorized(String orderId, String correlationId, String causationId,
                                         double amount, String currency, boolean duplicate) {

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("paymentId", "p-" + CorrelationIds.newId().substring(0, 8));
        data.put("amount", amount);
        data.put("currency", currency);

        var env = EventEnvelope.of(EventType.PaymentAuthorized, CorrelationIds.newId(), correlationId, causationId, "payment-service", data);
        kafka.send(paymentsEvents, orderId, env);
        log.info("Published PaymentAuthorized orderId={} correlationId={}", orderId, correlationId);

        if (duplicate) {
            kafka.send(paymentsEvents, orderId, env);
            log.warn("Published DUPLICATE PaymentAuthorized orderId={} correlationId={}", orderId, correlationId);
        }
    }

    public void publishPaymentFailed(String orderId, String correlationId, String causationId,
                                     String reason, boolean duplicate) {

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reason", reason);

        var env = EventEnvelope.of(EventType.PaymentFailed, CorrelationIds.newId(), correlationId, causationId, "payment-service", data);
        kafka.send(paymentsEvents, orderId, env);
        log.info("Published PaymentFailed orderId={} correlationId={} reason={}", orderId, correlationId, reason);

        if (duplicate) {
            kafka.send(paymentsEvents, orderId, env);
            log.warn("Published DUPLICATE PaymentFailed orderId={} correlationId={}", orderId, correlationId);
        }
    }

    public void publishRefundCompleted(String orderId, String correlationId, String causationId,
                                       String reason, boolean duplicate) {

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("refundId", "r-" + CorrelationIds.newId().substring(0, 8));
        data.put("reason", reason);

        var env = EventEnvelope.of(EventType.RefundCompleted, CorrelationIds.newId(), correlationId, causationId, "payment-service", data);
        kafka.send(paymentsEvents, orderId, env);
        log.info("Published RefundCompleted orderId={} correlationId={}", orderId, correlationId);

        if (duplicate) {
            kafka.send(paymentsEvents, orderId, env);
            log.warn("Published DUPLICATE RefundCompleted orderId={} correlationId={}", orderId, correlationId);
        }
    }

    public void publishRefundFailed(String orderId, String correlationId, String causationId,
                                    String reason, boolean duplicate) {

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reason", reason);

        var env = EventEnvelope.of(EventType.RefundFailed, CorrelationIds.newId(), correlationId, causationId, "payment-service", data);
        kafka.send(paymentsEvents, orderId, env);
        log.info("Published RefundFailed orderId={} correlationId={} reason={}", orderId, correlationId, reason);

        if (duplicate) {
            kafka.send(paymentsEvents, orderId, env);
            log.warn("Published DUPLICATE RefundFailed orderId={} correlationId={}", orderId, correlationId);
        }
    }
}
