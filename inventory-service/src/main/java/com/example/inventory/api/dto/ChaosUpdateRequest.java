package com.example.inventory.api.dto;

public record ChaosUpdateRequest(
        Integer fixedDelayMs,
        Double failureRate
) {}
