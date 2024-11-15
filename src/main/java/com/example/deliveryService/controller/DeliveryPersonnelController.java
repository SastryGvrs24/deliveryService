package com.example.deliveryService.controller;

import com.example.deliveryService.domain.DeliveryPersonnel;
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

@RestController
@RequestMapping("/api/deliveryPersonnel")
public class DeliveryPersonnelController {

    @Autowired
    private DeliveryPersonnelService deliveryPersonnelService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register Delivery Personnel (Sign-Up)
	@PostMapping("/signup")
	public ResponseEntity<Response<DeliveryPersonnel>> registerDeliveryPesonnel(@RequestBody DeliveryPersonnel deliveryPersonnel) {
		Response<DeliveryPersonnel> response = new Response<>();
		try {
			// Check if the username is already taken
			boolean usernameExists = deliveryPersonnelService.isUsernameAvailable(deliveryPersonnel.getUsername());
			if (usernameExists) {
				response.setResponseCode(HttpStatus.BAD_REQUEST);
				response.setErrorMessage("Username is already taken. Please choose another one.");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Assign the default role (ROLE_CUSTOMER)
			deliveryPersonnel.setPassword(passwordEncoder.encode(deliveryPersonnel.getPassword()));
			deliveryPersonnelService.registerDeliveryPersonnel(deliveryPersonnel);

			response.setResponseCode(HttpStatus.CREATED);
			response.setData(deliveryPersonnel);
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


    // View Available Deliveries
    @GetMapping("/availableDeliveries/{deliveryPersonnelId}")
    public ResponseEntity<Response<List<DeliveryOrder>>> viewAvailableDeliveries(@PathVariable Long deliveryPersonnelId) {
        Response<List<DeliveryOrder>> response = new Response<>();
        try {
            List<DeliveryOrder> availableDeliveries = deliveryPersonnelService.viewAvailableDeliveries(deliveryPersonnelId);
            response.setResponseCode(HttpStatus.OK);
            response.setData(availableDeliveries);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Failed to retrieve available deliveries: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Accept Delivery Order
    @PostMapping("/acceptDelivery/{deliveryOrderId}/{deliveryPersonnelId}")
    public ResponseEntity<Response<DeliveryOrder>> acceptDelivery(@PathVariable Long deliveryOrderId, @PathVariable Long deliveryPersonnelId) {
        Response<DeliveryOrder> response = new Response<>();
        try {
            DeliveryOrder acceptedDelivery = deliveryPersonnelService.acceptDelivery(deliveryOrderId, deliveryPersonnelId);
            response.setResponseCode(HttpStatus.OK);
            response.setData(acceptedDelivery);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Failed to accept delivery: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Update Delivery Status
    @PostMapping("/updateDeliveryStatus/{deliveryOrderId}/{status}")
    public ResponseEntity<Response<String>> updateDeliveryStatus(@PathVariable Long deliveryOrderId, @PathVariable String status) {
        Response<String> response = new Response<>();
        try {
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
    @PostMapping("/setAvailability/{deliveryPersonnelId}/{available}")
    public ResponseEntity<Response<String>> setDeliveryAvailability(@PathVariable Long deliveryPersonnelId, @PathVariable boolean available) {
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
