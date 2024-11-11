package com.example.deliveryService.service;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.RoleEnum;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.repository.CustomerRepository;
import com.example.deliveryService.repository.RestaurantOwnerRepository;
import com.example.deliveryService.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantOwnerService {

	@Autowired
	private RestaurantOwnerRepository restaurantOwnerRepository;

	@Autowired
	private JWTService jwtService;

	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;

	@Autowired
	private RoleRepository roleRepository;

	public RestaurantOwner registerRestaurantOwner(RestaurantOwner restaurantOwner) {
		Role restaurantOwnerRole = roleRepository.findByRoleName(RoleEnum.ROLE_RESTAURANT_OWNER.toString());

		if (restaurantOwnerRole == null) {
			// If the role doesn't exist in the DB, you could optionally create a new Role
			// entity and save it
			restaurantOwnerRole = new Role(RoleEnum.ROLE_RESTAURANT_OWNER.toString());
			roleRepository.save(restaurantOwnerRole);
		}

		// Assign the role to the customer
		restaurantOwner.setRoles(List.of(restaurantOwnerRole)); // Assign the role entity

		return restaurantOwnerRepository.save(restaurantOwner);
	}

	public RestaurantOwner findRestaurantOwnerByUsername(String username) {
		return restaurantOwnerRepository.findByUsername(username);
	}

	public RestaurantOwner updateRestaurantOwner(RestaurantOwner restaurantOwner) {
		return restaurantOwnerRepository.save(restaurantOwner);
	}

	public boolean existsByUsername(String username) {
		return restaurantOwnerRepository.existsByUsername(username);
	}

	// Method to load user details by username (similar to loadUserByUsername for
	// Customer)
	public UserDetails loadUserByUsername(String username) {
		RestaurantOwner restaurantOwner = restaurantOwnerRepository.findByUsername(username);
		if (restaurantOwner == null) {
			throw new UsernameNotFoundException("RestaurantOwner not found with username: " + username);
		}

		// You can add roles and authorities as needed
		return new User(restaurantOwner.getUsername(), restaurantOwner.getPassword(), new ArrayList<>());
	}

	public boolean isUsernameAvailable(String username) {
		// Check if a customer with the given username exists
		RestaurantOwner existingRestaurantOnwer = restaurantOwnerRepository.findByUsername(username);

		// If a customer is found with the given username, return false (username not
		// available)
		return existingRestaurantOnwer != null;
	}

	public LoginResponse authenticateRestaurantOwner(String username, String password) {
		// Authenticate using the authentication manager
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Fetch customer from the database by username
		RestaurantOwner authenticatedRestaurantOwner = restaurantOwnerRepository.findByUsername(username);

		// Extract roles from the authenticated customer
		List<String> roles = authenticatedRestaurantOwner.getRoles().stream().map(role -> role.getRoleName())
				.collect(Collectors.toList());

		// Generate JWT token including roles
		String token = jwtService.generateToken(username, roles);

		// Return LoginResponse with username, token, and roles
		return new LoginResponse(authenticatedRestaurantOwner.getUsername(), token, roles);
	}

}
