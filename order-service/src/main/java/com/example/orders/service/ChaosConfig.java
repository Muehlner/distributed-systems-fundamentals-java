package com.example.orders.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ChaosConfig {
    // Useful later if you want to add artificial delay/failure in order-service too.
    private final AtomicInteger fixedDelayMs = new AtomicInteger(0);

    public int getFixedDelayMs() { return fixedDelayMs.get(); }
    public void setFixedDelayMs(int ms) { fixedDelayMs.set(Math.max(ms, 0)); }
}
