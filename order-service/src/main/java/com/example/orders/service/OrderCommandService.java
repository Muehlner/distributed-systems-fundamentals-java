package com.example.orders.service;

import com.example.orders.api.dto.CreateOrderRequest;
import com.example.orders.domain.Order;
import com.example.orders.domain.OrderStatus;
import com.example.orders.messaging.producers.OrderEventPublisher;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher publisher;

    public OrderCommandService(OrderRepository orderRepository, OrderEventPublisher publisher) {
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    @Transactional
    public Order create(CreateOrderRequest req, String correlationId) {
        String orderId = "o-" + UUID.randomUUID();

        Order order = new Order(orderId, req.customerId(), req.amount(), req.currency(), correlationId);
        req.items().forEach(i -> order.addItem(i.sku(), i.qty()));
        orderRepository.save(order);

        List<Map<String, Object>> items = req.items().stream()
                .map(i -> Map.<String, Object>of("sku", i.sku(), "qty", i.qty()))
                .toList();

        // Publish events
        publisher.publishOrderCreated(orderId, correlationId, req.customerId(), req.amount(), req.currency(), items);
        publisher.publishAuthorizePayment(orderId, correlationId, req.amount(), req.currency());

        return order;
    }

    @Transactional
    public Order cancel(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        // For this lab, cancel is simple. You can make it more realistic later.
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Transactional
    public void onPaymentAuthorized(String orderId) {
        Order order = get(orderId);
        order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
        orderRepository.save(order);

        List<Map<String, Object>> items = order.getItems().stream()
                .map(i -> Map.<String, Object>of("sku", i.getSku(), "qty", i.getQty()))
                .toList();

        publisher.publishReserveInventory(orderId, order.getCorrelationId(), items);
    }

    @Transactional
    public void onPaymentFailed(String orderId) {
        Order order = get(orderId);
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);
    }

    @Transactional
    public void onInventoryReserved(String orderId) {
        Order order = get(orderId);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Transactional
    public void onInventoryFailed(String orderId, String reason) {
        Order order = get(orderId);
        order.setStatus(OrderStatus.INVENTORY_FAILED);
        orderRepository.save(order);

        // Compensation: request refund
        order.setStatus(OrderStatus.REFUND_REQUESTED);
        orderRepository.save(order);
        publisher.publishRefundRequested(orderId, order.getCorrelationId(), reason);
    }

    @Transactional
    public void onRefundCompleted(String orderId) {
        Order order = get(orderId);
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
    }

    private Order get(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
    }
}
