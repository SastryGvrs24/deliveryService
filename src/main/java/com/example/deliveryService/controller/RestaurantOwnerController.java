package com.example.deliveryService.controller;

import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.OrderStatus;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.item_Order;
import com.example.deliveryService.service.OrderService;
import com.example.deliveryService.service.RestaurantOwnerService;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantOwnerController {

	@Autowired
	private RestaurantOwnerService restaurantOwnerService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OrderService orderService;

	@PostMapping("/signup")
	public ResponseEntity<Response<RestaurantOwner>> registerRestaurantOwner(
			@RequestBody RestaurantOwner restaurantOwner) {
		Response<RestaurantOwner> response = new Response<>();
		try {
			// Check if the username is already taken
			boolean usernameExists = restaurantOwnerService.isUsernameAvailable(restaurantOwner.getUsername());
			if (usernameExists) {
				response.setResponseCode(HttpStatus.BAD_REQUEST);
				response.setErrorMessage("Username is already taken. Please choose another one.");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Encrypt password before saving
			restaurantOwner.setPassword(passwordEncoder.encode(restaurantOwner.getPassword()));
			// Set the role explicitly or through a method

			// Save the restaurant owner
			RestaurantOwner savedOwner = restaurantOwnerService.registerRestaurantOwner(restaurantOwner);

			// Prepare the response
			response.setResponseCode(HttpStatus.CREATED);
			response.setData(savedOwner);
			return new ResponseEntity<>(response, HttpStatus.CREATED);

		} catch (Exception e) {
			response.setResponseCode(HttpStatus.BAD_REQUEST);
			response.setErrorMessage("Sign-up failed: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Login Restaurant Owner
	@PostMapping("/login")
	public ResponseEntity<Response<LoginResponse>> loginRestaurantOwner(@RequestBody LoginRequest loginRequest) {
		Response<LoginResponse> response = new Response<>();
		try {
			// Authenticate user and generate token
			LoginResponse loginResponse = restaurantOwnerService.authenticateRestaurantOwner(loginRequest.getUsername(),
					loginRequest.getPassword());

			// Prepare the response with JWT token
			response.setResponseCode(HttpStatus.OK);
			response.setData(loginResponse);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Handle any authentication failures
			response.setResponseCode(HttpStatus.UNAUTHORIZED);
			response.setErrorMessage("Authentication failed: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

	// Check if the username is available
	@GetMapping("/checkUsernameAvailability")
	public ResponseEntity<Response<Boolean>> checkUsernameAvailability(@RequestParam String username) {
		Response<Boolean> response = new Response<>();
		boolean available = restaurantOwnerService.isUsernameAvailable(username);
		response.setResponseCode(HttpStatus.OK);
		response.setData(available);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Update Restaurant Details (Restaurant Owner Profile)
	@PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
	@PutMapping("/update")
	public ResponseEntity<Response<String>> updateRestaurantDetails(@RequestBody RestaurantOwner restaurantOwner) {
		Response<String> response = new Response<>();
		try {
			// Check if the restaurant owner exists by username
			RestaurantOwner existingOwner = restaurantOwnerService
					.findRestaurantOwnerByUsername(restaurantOwner.getUsername());
			if (existingOwner == null) {
				// If the restaurant owner does not exist, return an error message
				response.setResponseCode(HttpStatus.NOT_FOUND);
				response.setErrorMessage("Restaurant owner not found");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			// Update restaurant details only if they are not null
			if (restaurantOwner.getRestaurantName() != null) {
				existingOwner.setRestaurantName(restaurantOwner.getRestaurantName());
			}
			if (restaurantOwner.getAddress() != null) {
				existingOwner.setAddress(restaurantOwner.getAddress());
			}
			if (restaurantOwner.getHoursOfOperation() != null) {
				existingOwner.setHoursOfOperation(restaurantOwner.getHoursOfOperation());
			}

			// Update any other necessary fields here if applicable

			// Save updated restaurant owner details
			restaurantOwnerService.updateRestaurantOwner(existingOwner);

			response.setResponseCode(HttpStatus.OK);
			response.setData("Restaurant details updated successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.CONFLICT);
			response.setErrorMessage("Update failed: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@GetMapping("/owners")
	public ResponseEntity<Map<String, Object>> getAllRestaurantOwners() {
		try {
			// Fetch the list of restaurant owners from the service
			List<RestaurantOwner> owners = restaurantOwnerService.findAllRestaurantOwners();

			// Manually construct the JSON response
			List<Map<String, Object>> ownerList = owners.stream().map(owner -> {
				Map<String, Object> ownerMap = new HashMap<>();
				ownerMap.put("id", owner.getId());
				ownerMap.put("username", owner.getUsername());
				ownerMap.put("restaurantName", owner.getRestaurantName());
				ownerMap.put("address", owner.getAddress());
				ownerMap.put("hoursOfOperation", owner.getHoursOfOperation());

				// Get the roles of the owner and put them in a list (you can modify this
				// structure as needed)
				List<String> roles = owner.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList());
				ownerMap.put("roles", roles);

				return ownerMap;
			}).collect(Collectors.toList());

			// Wrap the list of restaurant owners in a response map
			Map<String, Object> response = new HashMap<>();
			response.put("responseCode", HttpStatus.OK);
			response.put("data", ownerList);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Handle error case
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("responseCode", HttpStatus.INTERNAL_SERVER_ERROR);
			errorResponse.put("errorMessage", "Failed to fetch restaurant owners: " + e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/owner/{id}")
	public ResponseEntity<Map<String, Object>> getRestaurantOwnerById(@PathVariable Long id) {
		try {
			// Fetch the restaurant owner by ID from the service
			RestaurantOwner restaurantOwner = restaurantOwnerService.findRestaurantOwnerById(id);

			if (restaurantOwner == null) {
				// Construct error response if the owner is not found
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("responseCode", HttpStatus.NOT_FOUND);
				errorResponse.put("errorMessage", "Restaurant owner not found with ID: " + id);
				return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
			}

			// Manually construct the JSON response
			Map<String, Object> ownerMap = new HashMap<>();
			ownerMap.put("id", restaurantOwner.getId());
			ownerMap.put("username", restaurantOwner.getUsername());
			ownerMap.put("restaurantName", restaurantOwner.getRestaurantName());
			ownerMap.put("address", restaurantOwner.getAddress());
			ownerMap.put("hoursOfOperation", restaurantOwner.getHoursOfOperation());

			// Get the roles of the owner and put them in a list (you can modify this
			// structure as needed)
			List<String> roles = restaurantOwner.getRoles().stream().map(Role::getRoleName)
					.collect(Collectors.toList());
			ownerMap.put("roles", roles);

			// Wrap the owner map in the response structure
			Map<String, Object> response = new HashMap<>();
			response.put("responseCode", HttpStatus.OK);
			response.put("data", ownerMap);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Handle error case if something goes wrong
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("responseCode", HttpStatus.INTERNAL_SERVER_ERROR);
			errorResponse.put("errorMessage", "Failed to fetch restaurant owner: " + e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
	@GetMapping("/orders")
	public ResponseEntity<Response<List<Map<String, Object>>>> getOrdersByStatus(
			@RequestParam(required = false) String status, @RequestParam Long restaurantOwnerId) {

		Response<List<Map<String, Object>>> response = new Response<>();

		try {
			// Fetch orders for the restaurant owner with the specified status
			List<item_Order> orders = orderService.findOrdersByRestaurantAndStatus(restaurantOwnerId, status);

			// Create a list of maps to represent the structured JSON response
			List<Map<String, Object>> orderDataList = new ArrayList<>();

			// Populate the structured response
			for (item_Order order : orders) {
				Map<String, Object> orderData = new HashMap<>();
				orderData.put("id", order.getId());
				orderData.put("status", order.getStatus());

				// Add customer data if needed
				Map<String, Object> customerData = new HashMap<>();
				customerData.put("id", order.getCustomer().getId());
				customerData.put("username", order.getCustomer().getUsername());
				orderData.put("customer", customerData);

				// Add restaurant owner data if needed
				Map<String, Object> restaurantOwnerData = new HashMap<>();
				restaurantOwnerData.put("id", order.getRestaurantOwner().getId());
				restaurantOwnerData.put("name", order.getRestaurantOwner().getRestaurantName());
				orderData.put("restaurantOwner", restaurantOwnerData);

				// Add menu items list with essential fields only
				List<Map<String, Object>> menuItemsData = new ArrayList<>();
				for (MenuItem menuItem : order.getMenuItems()) {
					Map<String, Object> menuItemData = new HashMap<>();
					menuItemData.put("id", menuItem.getId());
					menuItemData.put("name", menuItem.getName());
					menuItemData.put("price", menuItem.getPrice());
					menuItemsData.add(menuItemData);
				}
				orderData.put("menuItems", menuItemsData);

				// Add the structured order data to the response list
				orderDataList.add(orderData);
			}

			response.setResponseCode(HttpStatus.OK);
			response.setData(orderDataList);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
			response.setErrorMessage("Error fetching orders: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Endpoint to update the status of an order
	// Endpoint to update the status of an order
	@PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
	@PutMapping("/orders/{orderId}/status")
	public ResponseEntity<Response<String>> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status,
			@RequestParam Long restaurantOwnerId) {

		Response<String> response = new Response<>();

		try {
			// Validate if the provided status is a valid OrderStatus value
			OrderStatus orderStatus;
			try {
				orderStatus = OrderStatus.valueOf(status.toUpperCase());
			} catch (IllegalArgumentException e) {
				response.setResponseCode(HttpStatus.BAD_REQUEST);
				response.setErrorMessage(
						"Invalid status. Allowed values are: " + String.join(", ", OrderStatus.names()));
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Verify and update the order status if the order belongs to the restaurant
			// owner
			boolean updated = orderService.updateRestaurantOrderStatus(orderId, status.toUpperCase(),
					restaurantOwnerId);

			if (updated) {
				response.setResponseCode(HttpStatus.OK);
				response.setData("Order status updated successfully");
			} else {
				response.setResponseCode(HttpStatus.FORBIDDEN);
				response.setErrorMessage("You are not authorized to update this order");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
			response.setErrorMessage("Error updating order status: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
