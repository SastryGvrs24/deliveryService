package com.example.deliveryService.domain;

import jakarta.persistence.*;

@Entity
public class DeliveryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private item_Order order;  // The associated order

    @ManyToOne
    private DeliveryPersonnel deliveryPersonnel;  // The assigned delivery personnel

    private String status;  // Delivery status (e.g., "Picked up", "En route", "Delivered")

    // Constructor
    public DeliveryOrder(item_Order order, DeliveryPersonnel deliveryPersonnel, String status) {
        this.order = order;
        this.deliveryPersonnel = deliveryPersonnel;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public item_Order getOrder() {
        return order;
    }

    public void setOrder(item_Order order) {
        this.order = order;
    }

    public DeliveryPersonnel getDeliveryPersonnel() {
        return deliveryPersonnel;
    }

    public void setDeliveryPersonnel(DeliveryPersonnel deliveryPersonnel) {
        this.deliveryPersonnel = deliveryPersonnel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
