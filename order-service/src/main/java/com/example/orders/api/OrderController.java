package com.example.orders.api;

import com.example.orders.api.dto.*;
import com.example.orders.config.WebConfig;
import com.example.orders.domain.Order;
import com.example.orders.service.OrderCommandService;
import com.example.orders.service.OrderQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderCommandService commands;
    private final OrderQueryService queries;

    public OrderController(OrderCommandService commands, OrderQueryService queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CreateOrderResponse create(@RequestHeader(value = WebConfig.HEADER, required = false) String correlationId,
                                      @Valid @RequestBody CreateOrderRequest request) {
        Order created = commands.create(request, correlationId);
        return new CreateOrderResponse(created.getId(), created.getStatus().name(), created.getCorrelationId());
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable String orderId) {
        Order o = queries.get(orderId);
        return new OrderResponse(
                o.getId(),
                o.getStatus().name(),
                o.getCustomerId(),
                o.getAmount(),
                o.getCurrency(),
                o.getCorrelationId(),
                o.getCreatedAt(),
                o.getItems().stream().map(i -> new OrderResponse.Item(i.getSku(), i.getQty())).collect(Collectors.toList())
        );
    }

    @PostMapping("/{orderId}/cancel")
    public CancelOrderResponse cancel(@PathVariable String orderId) {
        Order o = commands.cancel(orderId);
        return new CancelOrderResponse(o.getId(), o.getStatus().name());
    }
}
