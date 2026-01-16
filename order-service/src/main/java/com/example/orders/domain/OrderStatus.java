package com.example.orders.domain;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_AUTHORIZED,
    PAYMENT_FAILED,
    PENDING_INVENTORY,
    COMPLETED,
    INVENTORY_FAILED,
    REFUND_REQUESTED,
    REFUNDED,
    CANCELLED
}
