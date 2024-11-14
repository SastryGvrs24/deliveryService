package com.example.deliveryService.controller;

import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.ResponseMessage;
import com.example.deliveryService.service.RestaurantOwnerService;
import com.example.deliveryService.repository.MenuItemRepository;

import java.util.HashMap;
import java.util.Map;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantMenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantOwnerService restaurantOwnerService;

    // Add Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PostMapping("/menu")
    public ResponseEntity<Object> addMenuItem(@RequestBody MenuItem menuItem) {
        try {
            menuItemRepository.save(menuItem);

            // Creating HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).getMenuItemById(menuItem.getId())).withSelfRel();
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).updateMenuItem(menuItem)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).deleteMenuItem(menuItem.getId())).withRel("delete");

            // Returning response with HATEOAS links
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseMessage("Menu item added successfully", selfLink, updateLink, deleteLink));
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Failed to add menu item", null), HttpStatus.BAD_REQUEST);
        }
    }

    // Update Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @PutMapping("/menu")
    public ResponseEntity<Object> updateMenuItem(@RequestBody MenuItem menuItem) {
        java.util.Optional<MenuItem> existingMenuItem = menuItemRepository.findById(menuItem.getId());
        if (!existingMenuItem.isPresent()) {
            return new ResponseEntity<>(new ResponseMessage("Menu item not found", null), HttpStatus.NOT_FOUND);
        }
        try {
            menuItemRepository.save(menuItem);

            // Creating HATEOAS links
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).getMenuItemById(menuItem.getId())).withSelfRel();
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).updateMenuItem(menuItem)).withRel("update");
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).deleteMenuItem(menuItem.getId())).withRel("delete");

            // Returning response with HATEOAS links
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage("Menu item updated successfully", selfLink, updateLink, deleteLink));
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Failed to update menu item", null), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete Menu Item
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Object> deleteMenuItem(@PathVariable Long id) {
        java.util.Optional<MenuItem> existingMenuItem = menuItemRepository.findById(id);
        if (!existingMenuItem.isPresent()) {
            return new ResponseEntity<>(new ResponseMessage("Menu item not found", null), HttpStatus.NOT_FOUND);
        }
        try {
            menuItemRepository.deleteById(id);

            // Creating HATEOAS links
            Link addLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RestaurantMenuController.class).addMenuItem(new MenuItem())).withRel("add");

            // Returning response with HATEOAS links
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage("Menu item deleted successfully", addLink));
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Failed to delete menu item", null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<Map<String, Object>> getMenuItemById(@PathVariable Long id) {
    	java.util.Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        
        Map<String, Object> response = new HashMap<>();
        
        if (menuItem.isPresent()) {
            MenuItem item = menuItem.get();

            // Manually construct the JSON structure
            Map<String, Object> menuItemData = new HashMap<>();
            menuItemData.put("id", item.getId());
            menuItemData.put("name", item.getName());
            menuItemData.put("description", item.getDescription());
            menuItemData.put("price", item.getPrice());

            // Avoid circular references by manually including only necessary parts of restaurant owner data
            if (item.getRestaurant() != null) {
                Map<String, Object> restaurantOwnerData = new HashMap<>();
                restaurantOwnerData.put("id", item.getRestaurant().getId());
                restaurantOwnerData.put("restaurant name", item.getRestaurant().getRestaurantName());

                // Adding restaurantOwner to menuItem data
                menuItemData.put("restaurant", restaurantOwnerData);
            }

            response.put("status", "success");
            response.put("message", "Menu item fetched successfully");
            response.put("data", menuItemData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "error");
            response.put("message", "Menu item not found with ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
