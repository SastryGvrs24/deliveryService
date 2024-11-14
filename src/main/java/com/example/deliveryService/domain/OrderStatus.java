package com.example.deliveryService.domain;

import java.util.Arrays;

public enum OrderStatus {
    PENDING,
    PREPARING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED;
    
    // Method to get all enum names as a list of strings
    public static String[] names() {
        return Arrays.stream(OrderStatus.values())
                     .map(Enum::name)
                     .toArray(String[]::new);
    }
}
