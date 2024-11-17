package com.example.deliveryService.service;

import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.domain.item_Order;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.RoleEnum;
import com.example.deliveryService.repository.CustomerRepository;
import com.example.deliveryService.repository.MenuItemRepository;
import com.example.deliveryService.repository.ItemOrderRepository;
import com.example.deliveryService.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ItemOrderRepository orderRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MenuItemRepository menuItemRepository;

	// Customer Registration: encodes the password and saves the customer
	public Customer registerCustomer(Customer customer) {

		// Create a Role entity for the customer
		Role customerRole = roleRepository.findByRoleName(RoleEnum.ROLE_CUSTOMER.toString());

		if (customerRole == null) {
			// If the role doesn't exist in the DB, you could optionally create a new Role
			// entity and save it
			customerRole = new Role(RoleEnum.ROLE_CUSTOMER.toString());
			roleRepository.save(customerRole);
		}

		// Assign the role to the customer
		customer.setRoles(List.of(customerRole)); // Assign the role entity

		return customerRepository.save(customer);
	}

	// Find Customer by Username
	public Customer getCustomerByUsername(String username) {
		return customerRepository.findByUsername(username);
	}


	public List<item_Order> placeOrder(Long customerId, String status, List<Map<String, Object>> menuItemsData) {
	    // Fetch the customer based on customerId
	    Customer customer = customerRepository.findById(customerId)
	        .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

	    // Group menu items by restaurant
	    Map<Long, List<MenuItem>> restaurantMenuItemsMap = new HashMap<>();
	    
	    for (Map<String, Object> itemData : menuItemsData) {
	        Long menuItemId = Long.parseLong(itemData.get("id").toString());
	        Long restaurantId = Long.parseLong(itemData.get("restaurantId").toString());

	        // Fetch MenuItem and validate it exists
	        MenuItem menuItem = menuItemRepository.findById(menuItemId)
	            .orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));
	        
	        // Ensure the menu item belongs to the specified restaurant
	        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
	            throw new IllegalArgumentException("MenuItem does not belong to the specified restaurant");
	        }

	        // Add the item to the map based on its restaurant
	        restaurantMenuItemsMap.computeIfAbsent(restaurantId, k -> new ArrayList<>()).add(menuItem);
	    }

	    // Create and save an order for each restaurant
	    List<item_Order> createdOrders = new ArrayList<>();
	    for (Map.Entry<Long, List<MenuItem>> entry : restaurantMenuItemsMap.entrySet()) {
	        item_Order order = new item_Order();
	        order.setCustomer(customer);
	        order.setStatus(status);
	        order.setMenuItems(entry.getValue());

	        // Optionally set the restaurant owner for tracking
	        RestaurantOwner restaurantOwner = entry.getValue().get(0).getRestaurant();
	        order.setRestaurantOwner(restaurantOwner);

	        // Save the order and add it to the result list
	        item_Order savedOrder = orderRepository.save(order);
	        createdOrders.add(savedOrder);
	    }

	    return createdOrders;  // Return the list of orders, each one associated with a different restaurant
	}



	// Login service: authenticates, generates JWT token, and constructs
	// LoginResponse
	public LoginResponse authenticateCustomer(String username, String password) {
		// Authenticate using the authentication manager
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Fetch customer from the database by username
		Customer authenticatedCustomer = customerRepository.findByUsername(username);

		// Extract roles from the authenticated customer
		List<String> roles = authenticatedCustomer.getRoles().stream().map(role -> role.getRoleName())
				.collect(Collectors.toList());

		// Generate JWT token including roles
		String token = jwtService.generateToken(username, roles);

		// Return LoginResponse with username, token, and roles
		return new LoginResponse(authenticatedCustomer.getUsername(), token, roles);
	}

	public boolean updateCustomer(Customer customer) {
		Customer existingCustomer = customerRepository.findByUsername(customer.getUsername());

		if (existingCustomer != null) {
			if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
				existingCustomer.setPassword(passwordEncoder.encode(customer.getPassword()));
			}
			if (customer.getFullName() != null) {
				existingCustomer.setFullName(customer.getFullName());
			}
			if (customer.getDeliveryAddress() != null) {
				existingCustomer.setDeliveryAddress(customer.getDeliveryAddress());
			}
			// Update any other required fields here

			customerRepository.save(existingCustomer);
			return true;
		}

		return false;
	}

	public List<MenuItem> searchMenuItems(String searchTerm, String searchType) {
	    switch (searchType.toLowerCase()) {
	        case "name":
	            return menuItemRepository.findByNameContainingIgnoreCaseAndAvailable(searchTerm, true);
	        case "cuisine":
	            return menuItemRepository.findByCuisineTypeContainingIgnoreCaseAndAvailable(searchTerm, true);
	        case "restaurant":
	            return menuItemRepository.findByRestaurantRestaurantNameContainingIgnoreCaseAndAvailable(searchTerm, true);
	        default:
	            throw new IllegalArgumentException("Invalid search type: " + searchType);
	    }
	}

	// New method to return all menu items when no search criteria is provided
	public List<MenuItem> getAllMenuItems() {
	    return menuItemRepository.findAll(); // Returns all menu items from all restaurants
	}


	public boolean isUsernameAvailable(String username) {
		// Check if a customer with the given username exists
		Customer existingCustomer = customerRepository.findByUsername(username);

		// If a customer is found with the given username, return false (username not
		// available)
		return existingCustomer != null;
	}
	
    public List<item_Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdWithMenuItems(customerId);
    }

}
