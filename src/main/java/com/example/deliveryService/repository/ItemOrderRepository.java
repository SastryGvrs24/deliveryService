package com.example.deliveryService.repository;

import com.example.deliveryService.domain.item_Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemOrderRepository extends JpaRepository<item_Order, Long> {

    // Find orders by customer ID
    List<item_Order> findByCustomerId(Long customerId);

    // Find orders by restaurant owner ID
    List<item_Order> findByRestaurantOwnerId(Long restaurantOwnerId);
    
    List<item_Order> findByRestaurantOwnerIdAndStatus(Long restaurantOwnerId, String status);

    item_Order findByIdAndRestaurantOwnerId(Long orderId, Long restaurantOwnerId);
    
    // Count orders grouped by status
    @Query("SELECT o.status, COUNT(o) FROM item_Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    // Count orders grouped by restaurant name
    @Query("SELECT r.restaurantName, COUNT(o) FROM item_Order o JOIN o.restaurantOwner r GROUP BY r.restaurantName")
    List<Object[]> countOrdersByRestaurant();
    
    @Query("SELECT COUNT(o) FROM item_Order o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT io FROM item_Order io " +
            "JOIN FETCH io.menuItems " +  // Ensure MenuItems are fetched with the order
            "WHERE io.customer.id = :customerId")
     List<item_Order> findByCustomerIdWithMenuItems(@Param("customerId") Long customerId);
}
