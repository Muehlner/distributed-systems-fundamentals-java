package com.example.payments.api;

import com.example.payments.api.dto.PaymentResponse;
import com.example.payments.domain.Payment;
import com.example.payments.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping("/{orderId}")
    public PaymentResponse get(@PathVariable String orderId) {
        Payment p = service.get(orderId);
        return new PaymentResponse(
                p.getOrderId(),
                p.getStatus().name(),
                p.getAmount(),
                p.getCurrency(),
                p.getCorrelationId(),
                p.getDetails(),
                p.getUpdatedAt()
        );
    }
}
