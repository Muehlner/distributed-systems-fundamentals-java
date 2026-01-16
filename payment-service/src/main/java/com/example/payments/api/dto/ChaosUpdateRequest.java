package com.example.payments.api.dto;

public record ChaosUpdateRequest(
        Integer fixedDelayMs,
        Double failureRate,
        Double duplicateEventRate
) {}
