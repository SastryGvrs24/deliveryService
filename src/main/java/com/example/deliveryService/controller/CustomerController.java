package com.example.deliveryService.controller;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.OrderStatus;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.item_Order;
import com.example.deliveryService.service.CustomerService;
import com.example.deliveryService.service.OrderService;
import com.example.deliveryService.service.RoleService;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;
import com.example.deliveryService.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private OrderService orderService;

	// Register a new Customer
	@PostMapping("/login")
	public ResponseEntity<Response<LoginResponse>> authenticateCustomer(@RequestBody LoginRequest loginRequest) {
		try {
			// Authenticate user and generate token
			LoginResponse loginResponse = customerService.authenticateCustomer(loginRequest.getUsername(),
					loginRequest.getPassword());

			// Prepare the response with JWT token
			Response<LoginResponse> response = new Response<>();
			response.setResponseCode(HttpStatus.OK);
			response.setData(loginResponse);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Handle any authentication failures
			Response<LoginResponse> errorResponse = new Response<>();
			errorResponse.setResponseCode(HttpStatus.UNAUTHORIZED);
			errorResponse.setErrorMessage("Authentication failed: " + e.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
		}
	}

	// Check if the username is available
	@GetMapping("/checkUsernameAvailability")
	public ResponseEntity<Response<Boolean>> checkUsernameAvailability(@RequestParam String username) {
		Response<Boolean> response = new Response<>();
		boolean available = customerService.isUsernameAvailable(username);
		response.setResponseCode(HttpStatus.OK);
		response.setData(available);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Customer Sign-up with Role assignment (e.g., ROLE_CUSTOMER)
	@PostMapping("/signup")
	public ResponseEntity<Response<Map<String, Object>>> registerCustomer(@RequestBody Customer customer) {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Check if the username is already taken
	        boolean usernameExists = customerService.isUsernameAvailable(customer.getUsername());
	        if (usernameExists) {
	            response.setResponseCode(HttpStatus.BAD_REQUEST);
	            response.setErrorMessage("Username is already taken. Please choose another one.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }

	        // Encrypt password before saving
	        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
	        
	        // Save the customer
	        Customer savedCustomer = customerService.registerCustomer(customer);

	        // Prepare the manual response structure
	        Map<String, Object> data = new HashMap<>();
	        data.put("id", savedCustomer.getId());
	        data.put("username", savedCustomer.getUsername());
	        data.put("fullName", savedCustomer.getFullName());
	        
	        // Extract role details from the saved customer (assuming the roles are already loaded)
	        List<Map<String, Object>> roles = new ArrayList<>();
	        for (Role role : savedCustomer.getRoles()) {
	            Map<String, Object> roleMap = new HashMap<>();
	            roleMap.put("id", role.getId());
	            roleMap.put("roleName", role.getRoleName());
	            roles.add(roleMap);
	        }

	        data.put("roles", roles);

	        // Prepare the success message
	        String successMessage = "Customer registered successfully";

	        // Populate the response with success message and data
	        response.setResponseCode(HttpStatus.CREATED);
	        response.setData(data); // Set the structured data as part of the response
	        response.setMessage(successMessage);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);

	    } catch (Exception e) {
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Sign-up failed: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	/**
	 * Update customer details. A customer can update their profile details (name,
	 * delivery address, etc.).
	 */
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PutMapping("/update")
	public ResponseEntity<Response<String>> updateCustomer(@RequestBody Customer customer) {
		Response<String> response = new Response<>();
		try {
			// Check if the customer exists by username
			Customer existingCustomer = customerService.getCustomerByUsername(customer.getUsername());
			if (existingCustomer == null) {
				// If the customer does not exist, return an error message
				response.setData("Customer not found");
				response.setResponseCode(HttpStatus.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			// Save updated customer details
			customerService.updateCustomer(existingCustomer);

			response.setData("Customer details updated successfully");
			response.setResponseCode(HttpStatus.OK);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setData("Failed to update customer details: " + e.getMessage());
			response.setResponseCode(HttpStatus.CONFLICT);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@PostMapping("/Order")
	public ResponseEntity<Response<List<Map<String, Object>>>> placeOrder(@RequestBody Map<String, Object> orderData) {
		Response<List<Map<String, Object>>> response = new Response<>();
		try {
			// Extract order data
			Long customerId = Long.parseLong(orderData.get("customerId").toString());
			String status = OrderStatus.PENDING.name();

			List<Map<String, Object>> menuItemsData = (List<Map<String, Object>>) orderData.get("menuItems");

			// Call the service to place the orders, each related to a different restaurant
			List<item_Order> placedOrders = customerService.placeOrder(customerId, status, menuItemsData);

			// Build response for each order
			List<Map<String, Object>> ordersResponseList = new ArrayList<>();
			for (item_Order placedOrder : placedOrders) {
				Map<String, Object> orderResponse = new HashMap<>();
				orderResponse.put("orderId", placedOrder.getId());
				orderResponse.put("status", placedOrder.getStatus());

				// Customer details
				Map<String, Object> customerData = new HashMap<>();
				customerData.put("id", placedOrder.getCustomer().getId());
				customerData.put("name", placedOrder.getCustomer().getUsername());
				orderResponse.put("customer", customerData);

				// Menu items and restaurant details for this order
				List<Map<String, Object>> orderedItems = new ArrayList<>();
				for (MenuItem menuItem : placedOrder.getMenuItems()) {
					Map<String, Object> itemData = new HashMap<>();
					itemData.put("id", menuItem.getId());
					itemData.put("name", menuItem.getName());
					itemData.put("price", menuItem.getPrice());
					itemData.put("cuisineType", menuItem.getCuisineType());

					// Restaurant information
					Map<String, Object> restaurantData = new HashMap<>();
					restaurantData.put("id", menuItem.getRestaurant().getId());
					restaurantData.put("restaurantName", menuItem.getRestaurant().getRestaurantName());
					itemData.put("restaurant", restaurantData);

					orderedItems.add(itemData);
				}
				orderResponse.put("menuItems", orderedItems);

				ordersResponseList.add(orderResponse); // Add this order's response to the list
			}

			response.setResponseCode(HttpStatus.CREATED);
			response.setData(ordersResponseList);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.BAD_REQUEST);
			response.setErrorMessage("Order placement failed: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// View Order History
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/orders/{customerId}")
	public ResponseEntity<Response<List<Map<String, Object>>>> viewOrderHistory(@PathVariable Long customerId) {
	    Response<List<Map<String, Object>>> response = new Response<>();
	    try {
	        // Fetch the orders by customer ID
	        List<item_Order> orders = customerService.getOrdersByCustomerId(customerId);

	        // Transform the orders into a clean JSON response structure
	        List<Map<String, Object>> orderResponses = new ArrayList<>();
	        for (item_Order order : orders) {
	            Map<String, Object> orderData = new HashMap<>();
	            orderData.put("orderId", order.getId());
	            orderData.put("status", order.getStatus());

	            // Include only basic customer info to avoid recursion
	            Map<String, Object> customerData = new HashMap<>();
	            customerData.put("id", order.getCustomer().getId());
	            customerData.put("username", order.getCustomer().getUsername());
	            orderData.put("customer", customerData);

	            // Add menu item details without recursive restaurant or order references
	            List<Map<String, Object>> menuItemsData = new ArrayList<>();
	            for (MenuItem item : order.getMenuItems()) {
	                Map<String, Object> itemData = new HashMap<>();
	                itemData.put("id", item.getId());
	                itemData.put("name", item.getName());
	                itemData.put("price", item.getPrice());
	                itemData.put("cuisineType", item.getCuisineType());
	                itemData.put("available", item.isAvailable());

	                // Basic restaurant details only
	                Map<String, Object> restaurantData = new HashMap<>();
	                restaurantData.put("id", item.getRestaurant().getId());
	                restaurantData.put("restaurantName", item.getRestaurant().getRestaurantName());
	                itemData.put("restaurant", restaurantData);

	                menuItemsData.add(itemData);
	            }
	            orderData.put("menuItems", menuItemsData);

	            orderResponses.add(orderData);
	        }

	        response.setResponseCode(HttpStatus.OK);
	        response.setData(orderResponses);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to retrieve orders: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}



	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/searchMenuItems")
	public ResponseEntity<Response<List<Map<String, Object>>>> searchMenuItems(@RequestParam(required = false) String searchTerm,
	        @RequestParam(required = false) String searchType) {
	    Response<List<Map<String, Object>>> response = new Response<>();
	    try {
	        List<MenuItem> menuItems;

	        // If no search term or type is provided, return all menu items
	        if (searchTerm == null || searchType == null) {
	            menuItems = customerService.getAllMenuItems();
	        } else {
	            // Get the list of MenuItems based on the search criteria
	            menuItems = customerService.searchMenuItems(searchTerm, searchType);
	        }

	        // Create a list to hold the response data
	        List<Map<String, Object>> menuItemResponses = new ArrayList<>();

	        // Transform MenuItem entities to a Map for the response
	        for (MenuItem menuItem : menuItems) {
	            Map<String, Object> menuItemData = new HashMap<>();
	            menuItemData.put("id", menuItem.getId());
	            menuItemData.put("name", menuItem.getName());
	            menuItemData.put("description", menuItem.getDescription());
	            menuItemData.put("price", menuItem.getPrice());
	            menuItemData.put("cuisineType", menuItem.getCuisineType());

	            // Restaurant information
	            if (menuItem.getRestaurant() != null) {
	                Map<String, Object> restaurantData = new HashMap<>();
	                restaurantData.put("id", menuItem.getRestaurant().getId());
	                restaurantData.put("restaurantName", menuItem.getRestaurant().getRestaurantName());
	                restaurantData.put("address", menuItem.getRestaurant().getAddress());
	                restaurantData.put("hoursOfOperation", menuItem.getRestaurant().getHoursOfOperation());
	                menuItemData.put("restaurant", restaurantData);
	            }

	            menuItemResponses.add(menuItemData);
	        }

	        // Set the response data
	        response.setResponseCode(HttpStatus.OK);
	        response.setData(menuItemResponses);
	    } catch (IllegalArgumentException e) {
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage(e.getMessage());
	    }

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


	// Track Order Status
	@PreAuthorize("hasRole('ROLE_CUSTOMER')")
	@GetMapping("/trackOrder/{orderId}")
	public ResponseEntity<Response<Map<String, Object>>> trackOrderStatus(@PathVariable Long orderId,
			@AuthenticationPrincipal UserDetails currentUser) {
		Response<Map<String, Object>> response = new Response<>();

		try {
			
			// Retrieve the customer by username (or however UserDetails identifies them)
			Customer customer = customerRepository.findByUsername(currentUser.getUsername());

			// Verify that the order belongs to the customer and fetch its status
			item_Order orderOpt = orderService.getOrderDetails(orderId);
			if (orderOpt==null || !orderOpt.getCustomer().getId().equals(customer.getId())) {
				throw new IllegalArgumentException("Order not found or does not belong to the customer");
			}

			item_Order order = orderOpt;
			Map<String, Object> orderStatusData = new HashMap<>();
			orderStatusData.put("orderId", order.getId());
			orderStatusData.put("status", order.getStatus());
			orderStatusData.put("restaurantName", order.getRestaurantOwner().getRestaurantName());

			response.setResponseCode(HttpStatus.OK);
			response.setData(orderStatusData);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.setResponseCode(HttpStatus.BAD_REQUEST);
			response.setErrorMessage("Failed to retrieve order status: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}
}
