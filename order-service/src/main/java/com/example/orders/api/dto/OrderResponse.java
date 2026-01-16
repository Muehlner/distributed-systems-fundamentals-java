package com.example.orders.api.dto;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderId,
        String status,
        String customerId,
        double amount,
        String currency,
        String correlationId,
        Instant createdAt,
        List<Item> items
) {
    public record Item(String sku, int qty) {}
}
