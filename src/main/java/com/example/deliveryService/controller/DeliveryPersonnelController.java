package com.example.deliveryService.controller;

import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.service.DeliveryPersonnelService;
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

    // Register Delivery Personnel
    @PostMapping("/signup")
    public ResponseEntity<Response<DeliveryPersonnel>> registerDeliveryPersonnel(@RequestBody DeliveryPersonnel deliveryPersonnel) {
        Response<DeliveryPersonnel> response = new Response<>();
        try {
            deliveryPersonnel.setPassword(passwordEncoder.encode(deliveryPersonnel.getPassword()));
            DeliveryPersonnel savedDeliveryPersonnel = deliveryPersonnelService.registerDeliveryPersonnel(deliveryPersonnel);
            response.setResponseCode(HttpStatus.CREATED);
            response.setData(savedDeliveryPersonnel);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Signup failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Login Delivery Personnel
    @PostMapping("/login")
    public ResponseEntity<Response<String>> loginDeliveryPersonnel(@RequestBody DeliveryPersonnel deliveryPersonnel) {
        Response<String> response = new Response<>();
        try {
            DeliveryPersonnel existingDeliveryPersonnel = deliveryPersonnelService.findDeliveryPersonnelByUsername(deliveryPersonnel.getUsername());
            if (existingDeliveryPersonnel == null || !passwordEncoder.matches(deliveryPersonnel.getPassword(), existingDeliveryPersonnel.getPassword())) {
                response.setResponseCode(HttpStatus.UNAUTHORIZED);
                response.setErrorMessage("Invalid credentials");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // Generate JWT token for the logged-in delivery personnel
            String token = "JWT-TOKEN"; // Generate the JWT token here using JWTService
            response.setResponseCode(HttpStatus.OK);
            response.setData(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Login failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
