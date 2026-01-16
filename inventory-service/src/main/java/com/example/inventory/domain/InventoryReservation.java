package com.example.inventory.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {

    @Id
    private String orderId;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private String details;

    protected InventoryReservation() {}

    public InventoryReservation(String orderId, InventoryStatus status, String correlationId, String details) {
        this.orderId = orderId;
        this.status = status;
        this.correlationId = correlationId;
        this.details = details == null ? "" : details;
        this.updatedAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; this.updatedAt = Instant.now(); }
    public String getCorrelationId() { return correlationId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; this.updatedAt = Instant.now(); }
    public Instant getUpdatedAt() { return updatedAt; }
}
