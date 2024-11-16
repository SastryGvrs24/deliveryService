package com.example.deliveryService.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class RestaurantOwner extends AppUser {

    private String restaurantName;
    private String address;
    private String hoursOfOperation;

    @OneToMany(mappedBy = "restaurantOwner")
    private List<item_Order> orders;

    public RestaurantOwner() {
        // Default constructor for JPA
    }

    public RestaurantOwner(String username, String password, String fullName, String restaurantName, String address, String hoursOfOperation) {
        super(username, password, fullName); // Call the constructor of the superclass
        this.restaurantName = restaurantName;
        this.address = address;
        this.hoursOfOperation = hoursOfOperation;
    }
    
    public RestaurantOwner(String username, String password) {
        super(username, password);
    }

    // Getters and Setters
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHoursOfOperation() {
        return hoursOfOperation;
    }

    public void setHoursOfOperation(String hoursOfOperation) {
        this.hoursOfOperation = hoursOfOperation;
    }

    public List<item_Order> getOrders() {
        return orders;
    }

    public void setOrders(List<item_Order> orders) {
        this.orders = orders;
    }
}
