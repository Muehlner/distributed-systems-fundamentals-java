package com.example.orders.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int qty;

    protected OrderItem() {}

    public OrderItem(Order order, String sku, int qty) {
        this.order = order;
        this.sku = sku;
        this.qty = qty;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
}
