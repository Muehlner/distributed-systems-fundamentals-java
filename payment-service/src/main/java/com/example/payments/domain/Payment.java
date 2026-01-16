package com.example.payments.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private String details;

    protected Payment() {}

    public Payment(String orderId, PaymentStatus status, double amount, String currency, String correlationId, String details) {
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.correlationId = correlationId;
        this.details = details == null ? "" : details;
        this.updatedAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; this.updatedAt = Instant.now(); }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCorrelationId() { return correlationId; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; this.updatedAt = Instant.now(); }
}
