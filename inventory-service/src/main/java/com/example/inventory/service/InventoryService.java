package com.example.inventory.service;

import com.example.inventory.domain.InventoryReservation;
import com.example.inventory.domain.InventoryStatus;
import com.example.inventory.messaging.producers.InventoryEventsPublisher;
import com.example.inventory.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class InventoryService {

    private final InventoryReservationRepository repo;
    private final InventoryEventsPublisher publisher;
    private final ChaosConfig chaos;
    private final Random rnd = new Random();

    public InventoryService(InventoryReservationRepository repo,
                            InventoryEventsPublisher publisher,
                            ChaosConfig chaos) {
        this.repo = repo;
        this.publisher = publisher;
        this.chaos = chaos;
    }

    @Transactional
    public void reserve(String orderId, String correlationId, String causationId, List<Map<String, Object>> items) {
        applyChaosDelay();

        boolean shouldFail = rnd.nextDouble() < chaos.getFailureRate();
        if (shouldFail) {
            String reason = "OUT_OF_STOCK|SIMULATED";
            upsert(orderId, InventoryStatus.FAILED, correlationId, "reserve_failed:" + reason);
            publisher.publishReservationFailed(orderId, correlationId, causationId, reason);
            return;
        }

        upsert(orderId, InventoryStatus.RESERVED, correlationId, "reserved_items_count=" + (items == null ? 0 : items.size()));
        publisher.publishReserved(orderId, correlationId, causationId, items);
    }

    @Transactional
    public void release(String orderId, String correlationId, String causationId, String reason) {
        applyChaosDelay();
        InventoryReservation r = repo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found: " + orderId));

        r.setStatus(InventoryStatus.RELEASED);
        r.setDetails("released:" + (reason == null ? "N/A" : reason));
        repo.save(r);

        publisher.publishReleased(orderId, correlationId, causationId, reason);
    }

    public InventoryReservation get(String orderId) {
        return repo.findById(orderId).orElseThrow(() -> new NoSuchElementException("Reservation not found: " + orderId));
    }

    private void upsert(String orderId, InventoryStatus status, String correlationId, String details) {
        InventoryReservation r = repo.findById(orderId)
                .orElse(new InventoryReservation(orderId, status, correlationId, details));
        r.setStatus(status);
        r.setDetails(details);
        repo.save(r);
    }

    private void applyChaosDelay() {
        int delay = chaos.getFixedDelayMs();
        if (delay <= 0) return;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
