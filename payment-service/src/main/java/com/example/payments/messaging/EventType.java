package com.example.payments.messaging;

public enum EventType {
    // incoming commands
    AuthorizePayment,
    RefundRequested,

    // outgoing events
    PaymentAuthorized,
    PaymentFailed,
    RefundCompleted,
    RefundFailed
}
