package com.example.orders.messaging;

public enum EventType {
    // outgoing
    OrderCreated,
    AuthorizePayment,
    ReserveInventory,
    RefundRequested,

    // incoming payments
    PaymentAuthorized,
    PaymentFailed,
    RefundCompleted,
    RefundFailed,

    // incoming inventory
    InventoryReserved,
    InventoryReservationFailed
}
