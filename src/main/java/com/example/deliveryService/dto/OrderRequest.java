package com.example.deliveryService.dto;

import java.util.List;

public class OrderRequest {

    private Long customerId;
    private Long restaurantOwnerId;
    private List<Long> menuItemIds;  // List of menu item IDs

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantOwnerId() {
        return restaurantOwnerId;
    }

    public void setRestaurantOwnerId(Long restaurantOwnerId) {
        this.restaurantOwnerId = restaurantOwnerId;
    }

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }
}
