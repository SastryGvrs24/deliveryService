package com.example.deliveryService.service;

import com.example.deliveryService.domain.*;
import com.example.deliveryService.dto.OrderRequest;
import com.example.deliveryService.repository.ItemOrderRepository;
import com.example.deliveryService.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private ItemOrderRepository itemOrderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private DeliveryService deliveryService;

    // Place a new order
    public item_Order placeOrder(OrderRequest orderRequest) {
        item_Order order = new item_Order();
        Customer customer = new Customer();  // You would retrieve customer by ID in a real app
        order.setCustomer(customer);  // Set the customer
        order.setRestaurantOwner(new RestaurantOwner());  // Set restaurant owner
        List<MenuItem> menuItems = menuItemRepository.findAllById(orderRequest.getMenuItemIds());  // Get menu items
        order.setMenuItems(menuItems);  // Set menu items
        order.setStatus(OrderStatus.PENDING.toString());

        itemOrderRepository.save(order);

        // Optionally, assign delivery here

        return order;
    }

    // Get order details
    public item_Order getOrderDetails(Long orderId) {
        Optional<item_Order> order = itemOrderRepository.findById(orderId);
        return order.orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Get all orders by a customer
    public List<item_Order> getOrdersByCustomer(Long customerId) {
        return itemOrderRepository.findByCustomerId(customerId);
    }

    // Get all orders for a restaurant owner
    public List<item_Order> getOrdersByRestaurantOwner(Long restaurantOwnerId) {
        return itemOrderRepository.findByRestaurantOwnerId(restaurantOwnerId);
    }

    // Update the order status
    public item_Order updateOrderStatus(Long orderId, String status) {
        item_Order order = getOrderDetails(orderId);
        order.setStatus(status);
        return itemOrderRepository.save(order);
    }

}
