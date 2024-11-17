package com.example.deliveryService.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Customer extends AppUser {

    private String deliveryAddress;
    private String paymentDetails;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<item_Order> orders;

    public Customer() {
        // Default constructor for JPA
    }

    public Customer(String username, String password, String fullName, String deliveryAddress, String paymentDetails) {
        super(username, password, fullName); // Call the constructor of the superclass
        this.deliveryAddress = deliveryAddress;
        this.paymentDetails = paymentDetails;
    }
    public Customer(String username, String password) {
        super(username, password); 
    }
    // Getters and Setters
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public List<item_Order> getOrders() {
        return orders;
    }

    public void setOrders(List<item_Order> orders) {
        this.orders = orders;
    }
}
