package com.example.orders.service;

import com.example.orders.domain.Order;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class OrderQueryService {

    private final OrderRepository repo;

    public OrderQueryService(OrderRepository repo) {
        this.repo = repo;
    }

    public Order get(String orderId) {
        return repo.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
    }
}
