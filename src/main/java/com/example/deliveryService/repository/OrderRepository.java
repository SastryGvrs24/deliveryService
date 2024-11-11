package com.example.deliveryService.repository;

import com.example.deliveryService.domain.item_Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<item_Order, Long> {

    List<item_Order> findByCustomerId(Long customerId);
}
