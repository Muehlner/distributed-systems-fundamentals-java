package com.example.orders.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotEmpty List<Item> items,
        @Positive double amount,
        @NotBlank String currency
) {
    public record Item(@NotBlank String sku, @Positive int qty) {}
}
