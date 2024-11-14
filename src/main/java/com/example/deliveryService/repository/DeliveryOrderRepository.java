package com.example.deliveryService.repository;

import com.example.deliveryService.domain.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {

    // Find a delivery order by the associated order ID
    Optional<DeliveryOrder> findByOrderId(Long orderId);

	List<DeliveryOrder> findByDeliveryPersonnelIdAndStatus(Long deliveryPersonnelId, String string);
}
