package com.example.deliveryService.controller;

import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.service.RestaurantOwnerService;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;
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

    @PostMapping("/signup")
    public ResponseEntity<Response<RestaurantOwner>> registerRestaurantOwner(@RequestBody RestaurantOwner restaurantOwner) {
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
            LoginResponse loginResponse = restaurantOwnerService.authenticateRestaurantOwner(loginRequest.getUsername(), loginRequest.getPassword());

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
            RestaurantOwner existingOwner = restaurantOwnerService.findRestaurantOwnerByUsername(restaurantOwner.getUsername());
            if (existingOwner == null) {
                response.setResponseCode(HttpStatus.NOT_FOUND);
                response.setErrorMessage("Restaurant owner not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Update restaurant details
            existingOwner.setRestaurantName(restaurantOwner.getRestaurantName());
            existingOwner.setAddress(restaurantOwner.getAddress());
            existingOwner.setHoursOfOperation(restaurantOwner.getHoursOfOperation());
            restaurantOwnerService.updateRestaurantOwner(existingOwner);

            response.setResponseCode(HttpStatus.OK);
            response.setData("Restaurant details updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Update failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
