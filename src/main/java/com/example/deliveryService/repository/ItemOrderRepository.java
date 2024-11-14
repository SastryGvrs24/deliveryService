package com.example.deliveryService.repository;

import com.example.deliveryService.domain.item_Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOrderRepository extends JpaRepository<item_Order, Long> {

    // Find orders by customer ID
    List<item_Order> findByCustomerId(Long customerId);

    // Find orders by restaurant owner ID
    List<item_Order> findByRestaurantOwnerId(Long restaurantOwnerId);
    
    List<item_Order> findByRestaurantOwnerIdAndStatus(Long restaurantOwnerId, String status);

    item_Order findByIdAndRestaurantOwnerId(Long orderId, Long restaurantOwnerId);
}
