package com.example.orders.api;

import com.example.orders.service.ChaosConfig;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/chaos")
public class AdminChaosController {

    private final ChaosConfig chaos;

    public AdminChaosController(ChaosConfig chaos) {
        this.chaos = chaos;
    }

    public record ChaosUpdate(Integer fixedDelayMs) {}

    @PostMapping
    public void update(@RequestBody ChaosUpdate req) {
        if (req != null && req.fixedDelayMs != null) chaos.setFixedDelayMs(req.fixedDelayMs);
    }

    @GetMapping
    public ChaosUpdate get() {
        return new ChaosUpdate(chaos.getFixedDelayMs());
    }
}
