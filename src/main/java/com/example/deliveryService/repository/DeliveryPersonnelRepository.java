package com.example.deliveryService.repository;

import com.example.deliveryService.domain.DeliveryPersonnel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonnelRepository extends JpaRepository<DeliveryPersonnel, Long> {

	DeliveryPersonnel findByUsername(String username);
	
	List<DeliveryPersonnel> findByAvailable(boolean available);

}
