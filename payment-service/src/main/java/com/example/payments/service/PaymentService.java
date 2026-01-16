package com.example.payments.service;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.messaging.producers.PaymentEventsPublisher;
import com.example.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final PaymentEventsPublisher publisher;
    private final ChaosConfig chaos;
    private final Random rnd = new Random();

    public PaymentService(PaymentRepository repo,
                          PaymentEventsPublisher publisher,
                          ChaosConfig chaos) {
        this.repo = repo;
        this.publisher = publisher;
        this.chaos = chaos;
    }

    @Transactional
    public void authorize(String orderId, String correlationId, String causationId, double amount, String currency) {
        applyChaosDelay();

        boolean shouldFail = rnd.nextDouble() < chaos.getFailureRate();
        if (shouldFail) {
            String reason = "GATEWAY_TIMEOUT|SIMULATED";
            upsert(orderId, PaymentStatus.FAILED, amount, currency, correlationId, "authorize_failed:" + reason);
            publisher.publishPaymentFailed(orderId, correlationId, causationId, reason, shouldDuplicate());
            return;
        }

        upsert(orderId, PaymentStatus.AUTHORIZED, amount, currency, correlationId, "authorized");
        publisher.publishPaymentAuthorized(orderId, correlationId, causationId, amount, currency, shouldDuplicate());
    }

    @Transactional
    public void refund(String orderId, String correlationId, String causationId, String reason) {
        applyChaosDelay();

        Payment p = repo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Payment not found for orderId: " + orderId));

        boolean shouldFail = rnd.nextDouble() < chaos.getFailureRate();
        if (shouldFail) {
            String failReason = "REFUND_FAILED|SIMULATED";
            p.setStatus(PaymentStatus.REFUND_FAILED);
            p.setDetails("refund_failed:" + failReason);
            repo.save(p);

            publisher.publishRefundFailed(orderId, correlationId, causationId, failReason, shouldDuplicate());
            return;
        }

        p.setStatus(PaymentStatus.REFUNDED);
        p.setDetails("refunded:" + (reason == null ? "N/A" : reason));
        repo.save(p);

        publisher.publishRefundCompleted(orderId, correlationId, causationId, reason, shouldDuplicate());
    }

    public Payment get(String orderId) {
        return repo.findById(orderId).orElseThrow(() -> new NoSuchElementException("Payment not found: " + orderId));
    }

    private void upsert(String orderId, PaymentStatus status, double amount, String currency, String correlationId, String details) {
        Payment p = repo.findById(orderId)
                .orElse(new Payment(orderId, status, amount, currency, correlationId, details));
        p.setStatus(status);
        p.setDetails(details);
        repo.save(p);
    }

    private boolean shouldDuplicate() {
        return rnd.nextDouble() < chaos.getDuplicateEventRate();
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
