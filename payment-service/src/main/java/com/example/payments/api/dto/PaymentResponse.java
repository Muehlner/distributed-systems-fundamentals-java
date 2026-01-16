package com.example.payments.api.dto;

import java.time.Instant;

public record PaymentResponse(
        String orderId,
        String status,
        double amount,
        String currency,
        String correlationId,
        String details,
        Instant updatedAt
) {}
