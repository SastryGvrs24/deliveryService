package com.example.deliveryService.repository;

import com.example.deliveryService.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

	List<MenuItem> findByNameContainingIgnoreCase(String name);

	List<MenuItem> findByCuisineTypeContainingIgnoreCase(String cuisineType);

	List<MenuItem> findByRestaurantRestaurantNameContainingIgnoreCase(String restaurantName);

	List<MenuItem> findByNameContainingIgnoreCaseAndAvailable(String name, boolean available);

	List<MenuItem> findByCuisineTypeContainingIgnoreCaseAndAvailable(String cuisineType, boolean available);

	List<MenuItem> findByRestaurantRestaurantNameContainingIgnoreCaseAndAvailable(String restaurantName,
			boolean available);
}
