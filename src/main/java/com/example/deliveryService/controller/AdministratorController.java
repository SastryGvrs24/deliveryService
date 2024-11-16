package com.example.deliveryService.controller;

import com.example.deliveryService.domain.*;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;
import com.example.deliveryService.dto.UserDTO;
import com.example.deliveryService.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdministratorController {

	@Autowired
	private AdministratorService administratorService;
	@Autowired
	private PasswordEncoder passwordEncoder;
//	@Autowired
//	private Admin admin;

	// Register a new Customer
	@PostMapping("/login")
	public ResponseEntity<Response<LoginResponse>> authenticateAdmin(@RequestBody LoginRequest loginRequest) {
		try {
			// Authenticate user and generate token
			LoginResponse loginResponse = administratorService.authenticateAdmin(loginRequest.getUsername(),
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
		boolean available = administratorService.isUsernameAvailable(username);
		response.setResponseCode(HttpStatus.OK);
		response.setData(available);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Customer Sign-up with Role assignment (e.g., ROLE_CUSTOMER)
	@PostMapping("/signup")
	public ResponseEntity<Response<Map<String, Object>>> registerAdmin(@RequestBody Admin admin) {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Check if the username is already taken
	        boolean usernameExists = administratorService.isUsernameAvailable(admin.getUsername());
	        if (usernameExists) {
	            response.setResponseCode(HttpStatus.BAD_REQUEST);
	            response.setErrorMessage("Username is already taken. Please choose another one.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }

	        // Encrypt password before saving
	        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

	        // Save the admin
	        Admin savedAdmin = administratorService.registerAdmin(admin);

	        // Prepare the manual response structure
	        Map<String, Object> data = new HashMap<>();
	        data.put("id", savedAdmin.getId());
	        data.put("username", savedAdmin.getUsername());
	        data.put("fullName", savedAdmin.getFullName());
	        
	        // Extract role details from the saved admin (assuming the roles are already loaded)
	        List<Map<String, Object>> roles = new ArrayList<>();
	        for (Role role : savedAdmin.getRoles()) {
	            Map<String, Object> roleMap = new HashMap<>();
	            roleMap.put("id", role.getId());
	            roleMap.put("roleName", role.getRoleName());
	            roles.add(roleMap);
	        }

	        data.put("roles", roles);

	        // Prepare the success message
	        String successMessage = "Admin registered successfully";

	        // Populate the response with success message and structured data
	        response.setResponseCode(HttpStatus.CREATED);
	        response.setData(data); // Set the structured data as part of the response
	        response.setMessage(successMessage);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);

	    } catch (Exception e) {
	        // Handle error case
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Sign-up failed: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	// Manage Users
	@PostMapping("/createUser")
	public ResponseEntity<Response<Map<String, Object>>> createUser(@RequestBody UserDTO userDTO) {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Call the service to create the user
	        AppUser createdUser = administratorService.createUser(userDTO);

	        // Build a structured response manually
	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("id", createdUser.getId());
	        responseData.put("username", createdUser.getUsername());
	        responseData.put("fullName", createdUser.getFullName());

	        // Include roles in a simplified structure
	        List<Map<String, Object>> roles = createdUser.getRoles().stream()
	        	    .map(role -> {
	        	        Map<String, Object> roleMap = new HashMap<>();
	        	        roleMap.put("id", role.getId());
	        	        roleMap.put("roleName", role.getRoleName());
	        	        return roleMap;
	        	    })
	        	    .collect(Collectors.toList());

	        responseData.put("roles", roles);

	        // Prepare and return the response
	        response.setResponseCode(HttpStatus.CREATED);
	        response.setData(responseData);
	        response.setMessage("User created successfully");
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    } catch (Exception e) {
	        // Handle errors
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to create user: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	// Update User
	// Update User
	@PutMapping("/updateUser/{userId}")
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Call the service to update the user
	        AppUser updatedUser = administratorService.updateUser(userId, userDTO);

	        // Prepare a structured and non-recursive response
	        Map<String, Object> userData = new HashMap<>();
	        userData.put("id", updatedUser.getId());
	        userData.put("username", updatedUser.getUsername());
	        userData.put("fullName", updatedUser.getFullName());

	        // Simplify the roles
	        List<Map<String, Object>> roles = updatedUser.getRoles().stream()
	        	    .map(role -> {
	        	        Map<String, Object> roleMap = new HashMap<>();
	        	        roleMap.put("id", role.getId());
	        	        roleMap.put("roleName", role.getRoleName());
	        	        return roleMap;
	        	    })
	        	    .collect(Collectors.toList());

	        userData.put("roles", roles);

	        // Set the structured data in the response
	        response.put("responseCode", HttpStatus.OK.value());
	        response.put("data", userData);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        // Handle errors and prepare a structured error response
	        response.put("responseCode", HttpStatus.BAD_REQUEST.value());
	        response.put("errorMessage", "Failed to update user: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	@PostMapping("/deactivateUser/{userId}")
	public ResponseEntity<Response<String>> deactivateUser(@PathVariable Long userId) {
		Response<String> response = new Response<>();
		try {
			administratorService.deactivateUser(userId);
			response.setResponseCode(HttpStatus.OK);
			response.setData("User deactivated successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.BAD_REQUEST);
			response.setErrorMessage("Failed to deactivate user: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// View and Manage Orders
	@GetMapping("/viewAllOrders")
	public ResponseEntity<Map<String, Object>> viewAllOrders() {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Fetch orders
	        List<item_Order> orders = administratorService.viewAllOrders();

	        // Prepare a simplified, non-recursive response
	        List<Map<String, Object>> orderData = orders.stream()
	        	    .map(order -> {
	        	        Map<String, Object> orderMap = new HashMap<>();
	        	        orderMap.put("id", order.getId());
	        	        orderMap.put("status", order.getStatus());

	        	        // Simplify customer details
	        	        if (order.getCustomer() != null) {
	        	            Map<String, Object> customerMap = new HashMap<>();
	        	            customerMap.put("id", order.getCustomer().getId());
	        	            customerMap.put("username", order.getCustomer().getUsername());
	        	            customerMap.put("fullName", order.getCustomer().getFullName());
	        	            orderMap.put("customer", customerMap);
	        	        }

	        	        // Simplify restaurant owner details
	        	        if (order.getRestaurantOwner() != null) {
	        	            Map<String, Object> restaurantOwnerMap = new HashMap<>();
	        	            restaurantOwnerMap.put("id", order.getRestaurantOwner().getId());
	        	            restaurantOwnerMap.put("name", order.getRestaurantOwner().getRestaurantName());
	        	            orderMap.put("restaurantOwner", restaurantOwnerMap);
	        	        }

	        	        // Simplify menu items
	        	        if (order.getMenuItems() != null) {
	        	            List<Map<String, Object>> menuItems = order.getMenuItems().stream()
	        	                .map(menuItem -> {
	        	                    Map<String, Object> itemMap = new HashMap<>();
	        	                    itemMap.put("id", menuItem.getId());
	        	                    itemMap.put("name", menuItem.getName());
	        	                    itemMap.put("description", menuItem.getDescription());
	        	                    itemMap.put("price", menuItem.getPrice());
	        	                    itemMap.put("available", menuItem.isAvailable());
	        	                    itemMap.put("cuisineType", menuItem.getCuisineType());
	        	                    return itemMap;
	        	                })
	        	                .collect(Collectors.toList());
	        	            orderMap.put("menuItems", menuItems);
	        	        }

	        	        return orderMap;
	        	    })
	        	    .collect(Collectors.toList());


	        // Construct the response
	        response.put("responseCode", HttpStatus.OK.value());
	        response.put("data", orderData);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        // Handle errors
	        response.put("responseCode", HttpStatus.BAD_REQUEST.value());
	        response.put("errorMessage", "Failed to retrieve orders: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	@PostMapping("/deleteOrder/{orderId}")
	public ResponseEntity<Response<String>> cancelOrder(@PathVariable Long orderId) {
		Response<String> response = new Response<>();
		try {
			administratorService.cancelOrder(orderId);
			response.setResponseCode(HttpStatus.OK);
			response.setData("Order deleted successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setResponseCode(HttpStatus.BAD_REQUEST);
			response.setErrorMessage("Failed to cancel order: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/generateReport")
	public ResponseEntity<Response<Map<String, Object>>> generateReport() {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Get the report data from the service
	        Map<String, Object> report = administratorService.generateReport();

	        response.setResponseCode(HttpStatus.OK);
	        response.setData(report);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to generate report: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	// Monitor Platform Activity
	@GetMapping("/platformActivity")
	public ResponseEntity<Response<Map<String, Object>>> monitorPlatformActivity() {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Get the platform activity data from the service
	        Map<String, Object> activityReport = administratorService.monitorPlatformActivity();

	        response.setResponseCode(HttpStatus.OK);
	        response.setData(activityReport);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to monitor platform activity: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}

}
