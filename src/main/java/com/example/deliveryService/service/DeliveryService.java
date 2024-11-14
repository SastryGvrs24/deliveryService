package com.example.deliveryService.service;

import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.DeliveryStatus;
import com.example.deliveryService.domain.item_Order;
import com.example.deliveryService.repository.DeliveryOrderRepository;
import com.example.deliveryService.repository.DeliveryPersonnelRepository;
import com.example.deliveryService.repository.ItemOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private ItemOrderRepository itemOrderRepository;

    @Autowired
    private DeliveryPersonnelRepository deliveryPersonnelRepository;

    // Method to assign delivery to a delivery personnel
    public DeliveryOrder assignDelivery(Long orderId, Long deliveryPersonnelId) {
        // Fetch the order
        item_Order order = itemOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch the delivery personnel
        DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findById(deliveryPersonnelId)
                .orElseThrow(() -> new RuntimeException("Delivery Personnel not found"));

        // Create and save the delivery order
        DeliveryOrder deliveryOrder = new DeliveryOrder(order, deliveryPersonnel, DeliveryStatus.PENDING.toString());
        return deliveryOrderRepository.save(deliveryOrder);
    }

    // Method to track delivery status of an order
    public DeliveryOrder trackDelivery(Long orderId) {
        return deliveryOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for this order"));
    }

    // Method to update the delivery status
    public DeliveryOrder updateDeliveryStatus(Long deliveryOrderId, String status) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
                .orElseThrow(() -> new RuntimeException("Delivery Order not found"));

        // Update status
        deliveryOrder.setStatus(status);
        return deliveryOrderRepository.save(deliveryOrder);
    }

    // Method to check if the delivery personnel is available
    public boolean checkDeliveryPersonnelAvailability(Long deliveryPersonnelId) {
        Optional<DeliveryPersonnel> deliveryPersonnel = deliveryPersonnelRepository.findById(deliveryPersonnelId);
        return deliveryPersonnel.isPresent() && "AVAILABLE".equals(deliveryPersonnel.get().getStatus());
    }
}
