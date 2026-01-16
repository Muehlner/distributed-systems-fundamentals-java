package com.example.inventory.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ChaosConfig {

    private final AtomicInteger fixedDelayMs = new AtomicInteger(0);
    private final AtomicReference<Double> failureRate = new AtomicReference<>(0.0);

    public int getFixedDelayMs() { return fixedDelayMs.get(); }
    public void setFixedDelayMs(int ms) { fixedDelayMs.set(Math.max(ms, 0)); }

    public double getFailureRate() { return failureRate.get(); }
    public void setFailureRate(double rate) {
        double r = Math.max(0.0, Math.min(1.0, rate));
        failureRate.set(r);
    }
}
