package com.example.deliveryService.repository;

import com.example.deliveryService.domain.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {

    List<DeliveryOrder> findByDeliveryPersonnelIdAndStatus(Long deliveryPersonnelId, String status);
}
