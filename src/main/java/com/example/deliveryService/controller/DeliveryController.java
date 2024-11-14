package com.example.deliveryService.controller;

import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // Assign delivery to delivery personnel
    @PostMapping("/assign")
    public DeliveryOrder assignDelivery(@RequestParam Long orderId, @RequestParam Long deliveryPersonnelId) {
        return deliveryService.assignDelivery(orderId, deliveryPersonnelId);
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
}
