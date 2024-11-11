package com.example.deliveryService.controller;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.item_Order; 
import com.example.deliveryService.service.CustomerService;
import com.example.deliveryService.service.RoleService;
import com.example.deliveryService.dto.LoginRequest;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Autowired
    private RoleService roleService;

    // Register a new Customer
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> authenticateCustomer(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user and generate token
            LoginResponse loginResponse = customerService.authenticateCustomer(loginRequest.getUsername(), loginRequest.getPassword());

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
    public ResponseEntity<Response<Customer>> registerCustomer(@RequestBody Customer customer) {
        Response<Customer> response = new Response<>();
        try {
            // Check if the username is already taken
            boolean usernameExists = customerService.isUsernameAvailable(customer.getUsername());
            if (usernameExists) {
                response.setResponseCode(HttpStatus.BAD_REQUEST);
                response.setErrorMessage("Username is already taken. Please choose another one.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Assign the default role (ROLE_CUSTOMER)
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customerService.registerCustomer(customer);

            response.setResponseCode(HttpStatus.CREATED);
            response.setData(customer);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Sign-up failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }



    /**
     * Update customer details. A customer can update their profile details (name, delivery address, etc.).
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

            // Update password if it's present in the request body
            if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
                existingCustomer.setPassword(passwordEncoder.encode(customer.getPassword()));
            }

            // Update address, name, and other profile details if provided in the request
            if (customer.getDeliveryAddress() != null && !customer.getDeliveryAddress().isEmpty()) {
                existingCustomer.setDeliveryAddress(customer.getDeliveryAddress());
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

    // Place an Order
    @PostMapping("/placeOrder")
    public ResponseEntity<Response<item_Order>> placeOrder(@RequestBody item_Order order) {
        Response<item_Order> response = new Response<>();
        try {
            item_Order placedOrder = customerService.placeOrder(order);
            response.setResponseCode(HttpStatus.CREATED);
            response.setData(placedOrder);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Order placement failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // View Order History
    @GetMapping("/orders/{customerId}")
    public ResponseEntity<Response<List<item_Order>>> viewOrderHistory(@PathVariable Long customerId) {
        Response<List<item_Order>> response = new Response<>();
        try {
            List<item_Order> orders = customerService.getOrdersByCustomerId(customerId);
            response.setResponseCode(HttpStatus.OK);
            response.setData(orders);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage("Failed to retrieve orders: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Search Menus (can be a GET request with query parameters for filtering)
    @GetMapping("/searchMenuItems")
    public ResponseEntity<Response<List<MenuItem>>> searchMenuItems(@RequestParam String searchTerm, @RequestParam String searchType) {
        Response<List<MenuItem>> response = new Response<>();
        try {
            List<MenuItem> menuItems = customerService.searchMenuItems(searchTerm, searchType);
            response.setResponseCode(HttpStatus.OK);
            response.setData(menuItems);
        } catch (IllegalArgumentException e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            response.setErrorMessage(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
