package com.example.payments.api;

import com.example.payments.api.dto.ChaosUpdateRequest;
import com.example.payments.service.ChaosConfig;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/chaos")
public class AdminChaosController {

    private final ChaosConfig chaos;

    public AdminChaosController(ChaosConfig chaos) {
        this.chaos = chaos;
    }

    @PostMapping
    public void update(@RequestBody ChaosUpdateRequest req) {
        if (req == null) return;
        if (req.fixedDelayMs() != null) chaos.setFixedDelayMs(req.fixedDelayMs());
        if (req.failureRate() != null) chaos.setFailureRate(req.failureRate());
        if (req.duplicateEventRate() != null) chaos.setDuplicateEventRate(req.duplicateEventRate());
    }

    @GetMapping
    public ChaosUpdateRequest get() {
        return new ChaosUpdateRequest(chaos.getFixedDelayMs(), chaos.getFailureRate(), chaos.getDuplicateEventRate());
    }
}
