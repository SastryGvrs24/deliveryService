package com.example.deliveryService.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class DeliveryPersonnel extends AppUser {

    private String contactDetails;
    private String vehicleType;
    private boolean available;

    @OneToMany(mappedBy = "deliveryPersonnel")
    private List<DeliveryOrder> deliveryOrders;

    public DeliveryPersonnel() {
        // Default constructor for JPA
    }

    public DeliveryPersonnel(String username, String password, String fullName, String contactDetails, String vehicleType, boolean available) {
        super(username, password, fullName); // Call the constructor of the superclass
        this.contactDetails = contactDetails;
        this.vehicleType = vehicleType;
        this.available = available;
    }
    public DeliveryPersonnel(String username, String password) {
        super(username, password);
    }
    // Getters and Setters
    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<DeliveryOrder> getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(List<DeliveryOrder> deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }
}
