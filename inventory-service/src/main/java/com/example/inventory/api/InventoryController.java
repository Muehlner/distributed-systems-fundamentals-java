package com.example.inventory.api;

import com.example.inventory.api.dto.ReservationResponse;
import com.example.inventory.domain.InventoryReservation;
import com.example.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/reservations/{orderId}")
    public ReservationResponse get(@PathVariable String orderId) {
        InventoryReservation r = service.get(orderId);
        return new ReservationResponse(
                r.getOrderId(),
                r.getStatus().name(),
                r.getCorrelationId(),
                r.getDetails(),
                r.getUpdatedAt()
        );
    }
}
