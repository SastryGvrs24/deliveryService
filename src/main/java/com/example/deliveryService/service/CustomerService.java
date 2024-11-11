package com.example.deliveryService.service;

import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.MenuItem;
import com.example.deliveryService.domain.item_Order;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.RoleEnum;
import com.example.deliveryService.repository.CustomerRepository;
import com.example.deliveryService.repository.MenuItemRepository;
import com.example.deliveryService.repository.OrderRepository;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

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

	// Get Orders by Customer ID
	public List<item_Order> getOrdersByCustomerId(Long customerId) {
		return orderRepository.findByCustomerId(customerId);
	}

	// Place an Order for a Customer
	public item_Order placeOrder(item_Order order) {
		return orderRepository.save(order);
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
			return menuItemRepository.findByNameContainingIgnoreCase(searchTerm);
		case "cuisine":
			return menuItemRepository.findByCuisineTypeContainingIgnoreCase(searchTerm);
		case "restaurant":
			return menuItemRepository.findByRestaurantRestaurantNameContainingIgnoreCase(searchTerm);
		default:
			throw new IllegalArgumentException("Invalid search type: " + searchType);
		}
	}

	public boolean isUsernameAvailable(String username) {
		// Check if a customer with the given username exists
		Customer existingCustomer = customerRepository.findByUsername(username);

		// If a customer is found with the given username, return false (username not
		// available)
		return existingCustomer != null;
	}

}
