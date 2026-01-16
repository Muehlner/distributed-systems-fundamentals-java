package com.example.inventory.api.dto;

import java.time.Instant;

public record ReservationResponse(
        String orderId,
        String status,
        String correlationId,
        String details,
        Instant updatedAt
) {}
