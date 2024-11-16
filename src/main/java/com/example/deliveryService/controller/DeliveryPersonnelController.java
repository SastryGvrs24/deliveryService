package com.example.deliveryService.controller;

import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.service.DeliveryPersonnelService;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/deliveryPersonnel")
public class DeliveryPersonnelController {

    @Autowired
    private DeliveryPersonnelService deliveryPersonnelService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register Delivery Personnel (Sign-Up)
    @PostMapping("/signup")
    public ResponseEntity<Response<Map<String, Object>>> registerDeliveryPersonnel(@RequestBody DeliveryPersonnel deliveryPersonnel) {
        Response<Map<String, Object>> response = new Response<>();
        try {
            // Check if the username is already taken
            boolean usernameExists = deliveryPersonnelService.isUsernameAvailable(deliveryPersonnel.getUsername());
            if (usernameExists) {
                response.setResponseCode(HttpStatus.BAD_REQUEST);
                response.setErrorMessage("Username is already taken. Please choose another one.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Encrypt password before saving
            deliveryPersonnel.setPassword(passwordEncoder.encode(deliveryPersonnel.getPassword()));

            // Save the delivery personnel
            DeliveryPersonnel savedDeliveryPersonnel = deliveryPersonnelService.registerDeliveryPersonnel(deliveryPersonnel);

            // Prepare the manual response structure
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedDeliveryPersonnel.getId());
            data.put("username", savedDeliveryPersonnel.getUsername());
            data.put("fullName", savedDeliveryPersonnel.getFullName());

            // Extract role details from the saved delivery personnel (assuming roles are already loaded)
            List<Map<String, Object>> roles = new ArrayList<>();
            for (Role role : savedDeliveryPersonnel.getRoles()) {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", role.getId());
                roleMap.put("roleName", role.getRoleName());
                roles.add(roleMap);
            }

            data.put("roles", roles);

            // Prepare the success message
            String successMessage = "Delivery personnel registered successfully";

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


	// Register a new Customer
	@PostMapping("/login")
	public ResponseEntity<Response<LoginResponse>> authenticateDeliveryPersonnel(@RequestBody LoginRequest loginRequest) {
		try {
			// Authenticate user and generate token
			LoginResponse loginResponse = deliveryPersonnelService.authenticateDeliveryPersonnel(loginRequest.getUsername(),
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
		boolean available = deliveryPersonnelService.isUsernameAvailable(username);
		response.setResponseCode(HttpStatus.OK);
		response.setData(available);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


    // View Available Deliveries
	@GetMapping("/availableDeliveries/{deliveryPersonnelId}")
	public ResponseEntity<Response<List<Map<String, Object>>>> viewAvailableDeliveries(@PathVariable Long deliveryPersonnelId) {
	    Response<List<Map<String, Object>>> response = new Response<>();
	    try {
	        // Fetch available deliveries from the service
	        List<DeliveryOrder> availableDeliveries = deliveryPersonnelService.viewAvailableDeliveries(deliveryPersonnelId);
	        
	        // Create a list to store the structured data
	        List<Map<String, Object>> deliveriesData = new ArrayList<>();
	        
	        // Loop through each delivery order and manually extract relevant data
	        for (DeliveryOrder deliveryOrder : availableDeliveries) {
	            Map<String, Object> orderData = new HashMap<>();
	            
	            // Extract order details
	            Map<String, Object> orderDetails = new HashMap<>();
	            orderDetails.put("id", deliveryOrder.getOrder().getId());

	            // Extract customer details (avoid recursion)
	            Map<String, Object> customerDetails = new HashMap<>();
	            customerDetails.put("id", deliveryOrder.getOrder().getCustomer().getId());
	            customerDetails.put("username", deliveryOrder.getOrder().getCustomer().getUsername());
	            customerDetails.put("fullName", deliveryOrder.getOrder().getCustomer().getFullName());
	            orderDetails.put("customer", customerDetails);

	            // Extract delivery personnel details (avoid recursion)
	            Map<String, Object> deliveryPersonnelDetails = new HashMap<>();
	            deliveryPersonnelDetails.put("id", deliveryOrder.getDeliveryPersonnel().getId());
	            deliveryPersonnelDetails.put("username", deliveryOrder.getDeliveryPersonnel().getUsername());
	            deliveryPersonnelDetails.put("fullName", deliveryOrder.getDeliveryPersonnel().getFullName());
	            deliveryPersonnelDetails.put("vehicleType", deliveryOrder.getDeliveryPersonnel().getVehicleType());
	            deliveryPersonnelDetails.put("contactDetails", deliveryOrder.getDeliveryPersonnel().getContactDetails());

	            // Add all the structured information to the main order map
	            orderData.put("order", orderDetails);
	            orderData.put("deliveryPersonnel", deliveryPersonnelDetails);
	            orderData.put("status", deliveryOrder.getStatus());

	            // Add this order data to the response list
	            deliveriesData.add(orderData);
	        }
	        
	        // Set the response code and data
	        response.setResponseCode(HttpStatus.OK);
	        response.setData(deliveriesData);
	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        // Handle error scenario
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to retrieve available deliveries: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	@PostMapping("/acceptDelivery")
	public ResponseEntity<Response<Map<String, Object>>> acceptDelivery(@RequestBody Map<String, Object> requestBody) {
	    Response<Map<String, Object>> response = new Response<>();
	    try {
	        // Extract fields from requestBody
	        Long deliveryOrderId = Long.valueOf(requestBody.get("deliveryOrderId").toString());
	        Long deliveryPersonnelId = Long.valueOf(requestBody.get("deliveryPersonnelId").toString());

	        // Call the service to accept the delivery
	        DeliveryOrder acceptedDelivery = deliveryPersonnelService.acceptDelivery(deliveryOrderId, deliveryPersonnelId);

	        // Manually structure the response to avoid recursion
	        Map<String, Object> responseData = new HashMap<>();
	        
	        // Extract basic details of the delivery order
	        Map<String, Object> orderDetails = new HashMap<>();
	        orderDetails.put("id", acceptedDelivery.getId());
	        
	        // Extract the order (customer) details
	        Map<String, Object> customerDetails = new HashMap<>();
	        customerDetails.put("id", acceptedDelivery.getOrder().getCustomer().getId());
	        customerDetails.put("username", acceptedDelivery.getOrder().getCustomer().getUsername());
	        customerDetails.put("fullName", acceptedDelivery.getOrder().getCustomer().getFullName());
	        orderDetails.put("customer", customerDetails);

	        // Extract delivery personnel details
	        Map<String, Object> deliveryPersonnelDetails = new HashMap<>();
	        deliveryPersonnelDetails.put("id", acceptedDelivery.getDeliveryPersonnel().getId());
	        deliveryPersonnelDetails.put("username", acceptedDelivery.getDeliveryPersonnel().getUsername());
	        deliveryPersonnelDetails.put("fullName", acceptedDelivery.getDeliveryPersonnel().getFullName());

	        // Set order and delivery personnel details in response
	        responseData.put("order", orderDetails);
	        responseData.put("deliveryPersonnel", deliveryPersonnelDetails);
	        responseData.put("status", acceptedDelivery.getStatus());

	        // Set the response code and data
	        response.setResponseCode(HttpStatus.OK);
	        response.setData(responseData);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        // Handle error scenario
	        response.setResponseCode(HttpStatus.BAD_REQUEST);
	        response.setErrorMessage("Failed to accept delivery: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


    @PostMapping("/updateDeliveryStatus")
    public ResponseEntity<Response<String>> updateDeliveryStatus(@RequestBody Map<String, Object> requestBody) {
        Response<String> response = new Response<>();
        try {
            // Extract fields from requestBody
            Long deliveryOrderId = Long.valueOf(requestBody.get("deliveryOrderId").toString());
            String status = requestBody.get("status").toString();

            // Call the service with extracted parameters
            deliveryPersonnelService.updateDeliveryStatus(deliveryOrderId, status);

            response.setResponseCode(HttpStatus.OK);
            response.setData("Delivery status updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Failed to update delivery status: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


 // Set Delivery Personnel Availability
    @PostMapping("/setAvailability/{deliveryPersonnelId}")
    public ResponseEntity<Response<String>> setDeliveryAvailability(
        @PathVariable Long deliveryPersonnelId, 
        @RequestParam boolean available) {  // Change from @PathVariable to @RequestParam for 'available'
        
        Response<String> response = new Response<>();
        try {
            deliveryPersonnelService.setDeliveryPersonnelAvailability(deliveryPersonnelId, available);
            response.setResponseCode(HttpStatus.OK);
            response.setData("Availability updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Failed to update availability: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
