package com.example.deliveryService.controller;

import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // Assign delivery to delivery personnel
    // Method to assign delivery to a delivery personnel
    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignDelivery(@RequestParam Long orderId, @RequestParam Long deliveryPersonnelId) {
        try {
            // Call the service method to assign the delivery
            DeliveryOrder deliveryOrder = deliveryService.assignDelivery(orderId, deliveryPersonnelId);

            // Manually build the response data to prevent recursion
            Map<String, Object> responseData = new HashMap<>();

            // Extract order details
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", deliveryOrder.getOrder().getId());

            // Extract customer details
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("id", deliveryOrder.getOrder().getCustomer().getId());
            customerData.put("username", deliveryOrder.getOrder().getCustomer().getUsername());
            customerData.put("fullName", deliveryOrder.getOrder().getCustomer().getFullName());
            orderData.put("customer", customerData);

            responseData.put("order", orderData);

            // Extract delivery personnel details
            Map<String, Object> deliveryPersonnelData = new HashMap<>();
            deliveryPersonnelData.put("id", deliveryOrder.getDeliveryPersonnel().getId());
            deliveryPersonnelData.put("username", deliveryOrder.getDeliveryPersonnel().getUsername());
            deliveryPersonnelData.put("fullName", deliveryOrder.getDeliveryPersonnel().getFullName());

            responseData.put("deliveryPersonnel", deliveryPersonnelData);

            // Set the delivery status
            responseData.put("status", deliveryOrder.getStatus());

            // Return the structured response with status 200 OK
            return new ResponseEntity<>(responseData, HttpStatus.OK);

        } catch (Exception e) {
            // If an error occurs, return a bad request response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to assign delivery: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Track delivery status
    @GetMapping("/{orderId}/track")
    public DeliveryOrder trackDelivery(@PathVariable Long orderId) {
        return deliveryService.trackDelivery(orderId);
    }

    // Update delivery status
    @PutMapping("/{deliveryOrderId}/status")
    public DeliveryOrder updateDeliveryStatus(@PathVariable Long deliveryOrderId, @RequestParam String status) {
        return deliveryService.updateDeliveryStatus(deliveryOrderId, status);
    }

    // Check availability of delivery personnel
    @GetMapping("/checkAvailability/{deliveryPersonnelId}")
    public boolean checkAvailability(@PathVariable Long deliveryPersonnelId) {
        return deliveryService.checkDeliveryPersonnelAvailability(deliveryPersonnelId);
    }
    
    @GetMapping("/availableDeliveryPersonnels")
    public ResponseEntity<List<DeliveryPersonnel>> getAvailableDeliveryPersonnel() {
        List<DeliveryPersonnel> availableDeliveryPersonnel = deliveryService.getAvailableDeliveryPersonnel();
        return new ResponseEntity<>(availableDeliveryPersonnel, HttpStatus.OK);
    }
}
