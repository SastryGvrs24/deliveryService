package com.example.deliveryService.repository;

import com.example.deliveryService.domain.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {

	RestaurantOwner findByUsername(String username);

	boolean existsByUsername(String username);
}
