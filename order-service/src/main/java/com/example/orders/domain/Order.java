package com.example.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    public Order(String id, String customerId, double amount, String currency, String correlationId) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.correlationId = correlationId;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.createdAt = Instant.now();
    }

    public void addItem(String sku, int qty) {
        this.items.add(new OrderItem(this, sku, qty));
    }

    // getters/setters
    public String getId() { return id; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}
