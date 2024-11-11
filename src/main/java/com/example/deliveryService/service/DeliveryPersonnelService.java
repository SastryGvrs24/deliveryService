package com.example.deliveryService.service;

import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.repository.DeliveryOrderRepository;
import com.example.deliveryService.repository.DeliveryPersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryPersonnelService {

    @Autowired
    private DeliveryPersonnelRepository deliveryPersonnelRepository;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    public DeliveryPersonnel registerDeliveryPersonnel(DeliveryPersonnel deliveryPersonnel) {
        return deliveryPersonnelRepository.save(deliveryPersonnel);
    }

    public DeliveryPersonnel findDeliveryPersonnelByUsername(String username) {
        return deliveryPersonnelRepository.findByUsername(username);
    }

    public List<DeliveryOrder> viewAvailableDeliveries(Long deliveryPersonnelId) {
        // Returns available deliveries (status = "Picked up")
        return deliveryOrderRepository.findByDeliveryPersonnelIdAndStatus(deliveryPersonnelId, "Picked up");
    }

    public DeliveryOrder acceptDelivery(Long deliveryOrderId, Long deliveryPersonnelId) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
                .orElseThrow(() -> new RuntimeException("Delivery Order not found"));
        deliveryOrder.setDeliveryPersonnel(new DeliveryPersonnel(deliveryPersonnelId));
        deliveryOrder.setStatus("En route"); // Update status to "En route"
        return deliveryOrderRepository.save(deliveryOrder);
    }

    public void updateDeliveryStatus(Long deliveryOrderId, String status) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
                .orElseThrow(() -> new RuntimeException("Delivery Order not found"));
        deliveryOrder.setStatus(status); // e.g., "Delivered"
        deliveryOrderRepository.save(deliveryOrder);
    }

    public void setDeliveryPersonnelAvailability(Long deliveryPersonnelId, boolean available) {
        DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findById(deliveryPersonnelId)
                .orElseThrow(() -> new RuntimeException("Delivery Personnel not found"));
        deliveryPersonnel.setAvailable(available);
        deliveryPersonnelRepository.save(deliveryPersonnel);
    }
    
   


}
