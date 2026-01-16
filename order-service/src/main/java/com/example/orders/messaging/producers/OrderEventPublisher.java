package com.example.orders.messaging.producers;

import com.example.orders.messaging.EventEnvelope;
import com.example.orders.messaging.EventType;
import com.example.orders.util.CorrelationIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, EventEnvelope> kafka;

    @Value("${app.topics.ordersEvents}")
    private String ordersEvents;

    @Value("${app.topics.paymentsCommands}")
    private String paymentsCommands;

    @Value("${app.topics.inventoryCommands}")
    private String inventoryCommands;

    public OrderEventPublisher(KafkaTemplate<String, EventEnvelope> kafka) {
        this.kafka = kafka;
    }

    public void publishOrderCreated(String orderId, String correlationId, String customerId, double amount, String currency, List<Map<String, Object>> items) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("customerId", customerId);
        data.put("amount", amount);
        data.put("currency", currency);
        data.put("items", items);

        var env = EventEnvelope.of(EventType.OrderCreated, CorrelationIds.newId(), correlationId, null, "order-service", data);
        kafka.send(ordersEvents, orderId, env);
        log.info("Published OrderCreated orderId={} correlationId={}", orderId, correlationId);
    }

    public void publishAuthorizePayment(String orderId, String correlationId, double amount, String currency) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("amount", amount);
        data.put("currency", currency);
        data.put("paymentMethod", "SIMULATED");

        var env = EventEnvelope.of(EventType.AuthorizePayment, CorrelationIds.newId(), correlationId, null, "order-service", data);
        kafka.send(paymentsCommands, orderId, env);
        log.info("Published AuthorizePayment orderId={} correlationId={}", orderId, correlationId);
    }

    public void publishReserveInventory(String orderId, String correlationId, List<Map<String, Object>> items) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("items", items);

        var env = EventEnvelope.of(EventType.ReserveInventory, CorrelationIds.newId(), correlationId, null, "order-service", data);
        kafka.send(inventoryCommands, orderId, env);
        log.info("Published ReserveInventory orderId={} correlationId={}", orderId, correlationId);
    }

    public void publishRefundRequested(String orderId, String correlationId, String reason) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("reason", reason);

        var env = EventEnvelope.of(EventType.RefundRequested, CorrelationIds.newId(), correlationId, null, "order-service", data);
        kafka.send(paymentsCommands, orderId, env);
        log.info("Published RefundRequested orderId={} correlationId={}", orderId, correlationId);
    }
}
