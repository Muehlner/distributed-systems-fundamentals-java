package com.example.orders.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventEnvelope {
    public String eventId;
    public String eventType;
    public Instant occurredAt;
    public String correlationId;
    public String causationId;
    public String producer;
    public int version;
    public Map<String, Object> data;

    public static EventEnvelope of(EventType type,
                                   String eventId,
                                   String correlationId,
                                   String causationId,
                                   String producer,
                                   Map<String, Object> data) {
        EventEnvelope e = new EventEnvelope();
        e.eventId = eventId;
        e.eventType = type.name();
        e.occurredAt = Instant.now();
        e.correlationId = correlationId;
        e.causationId = causationId;
        e.producer = producer;
        e.version = 1;
        e.data = data;
        return e;
    }
}
