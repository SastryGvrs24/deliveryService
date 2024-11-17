package com.example.deliveryService.controller;

import com.example.deliveryService.domain.*;
import com.example.deliveryService.dto.OrderRequest;
import com.example.deliveryService.service.OrderService;
import com.example.deliveryService.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryService deliveryService;

    // Place an order
    @PostMapping
    public item_Order placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    // Get order details
    @GetMapping("/{orderId}")
    public item_Order getOrderDetails(@PathVariable Long orderId) {
        return orderService.getOrderDetails(orderId);
    }

    // Track order status
    @GetMapping("/{orderId}/track")
    public DeliveryOrder trackOrder(@PathVariable Long orderId) {
        return deliveryService.trackDelivery(orderId);
    }

    // Get all orders of a customer
    @GetMapping("/customer/{customerId}")
    public List<item_Order> getCustomerOrders(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    // Update order status
    @PutMapping("/{orderId}/status")
    public item_Order updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    // Get all orders for a restaurant owner
    @GetMapping("/restaurant/{restaurantOwnerId}")
    public List<item_Order> getRestaurantOrders(@PathVariable Long restaurantOwnerId) {
        return orderService.getOrdersByRestaurantOwner(restaurantOwnerId);
    }
}
