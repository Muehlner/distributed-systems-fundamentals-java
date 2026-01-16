package com.example.payments.util;

import java.util.UUID;

public final class CorrelationIds {
    private CorrelationIds() {}
    public static String newId() {
        return UUID.randomUUID().toString();
    }
}
