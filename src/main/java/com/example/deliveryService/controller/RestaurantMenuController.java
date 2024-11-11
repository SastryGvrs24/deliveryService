package com.example.deliveryService.controller;

import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.service.RestaurantOwnerService;
import com.example.deliveryService.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant/menu")
public class RestaurantMenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantOwnerService restaurantOwnerService;

    // Add Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PostMapping("/add")
    public ResponseEntity<String> addMenuItem(@RequestBody MenuItem menuItem) {
        try {
            menuItemRepository.save(menuItem);
            return new ResponseEntity<>("Menu item added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add menu item", HttpStatus.BAD_REQUEST);
        }
    }

    // Update Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PutMapping("/update")
    public ResponseEntity<String> updateMenuItem(@RequestBody MenuItem menuItem) {
        try {
            menuItemRepository.save(menuItem);
            return new ResponseEntity<>("Menu item updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update menu item", HttpStatus.BAD_REQUEST);
        }
    }

    // Delete Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id) {
        try {
            menuItemRepository.deleteById(id);
            return new ResponseEntity<>("Menu item deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete menu item", HttpStatus.BAD_REQUEST);
        }
    }
}
