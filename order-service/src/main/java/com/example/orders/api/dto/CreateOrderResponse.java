package com.example.orders.api.dto;

public record CreateOrderResponse(
        String orderId,
        String status,
        String correlationId
) {}
