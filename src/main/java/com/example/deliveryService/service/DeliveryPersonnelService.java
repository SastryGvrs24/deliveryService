package com.example.deliveryService.service;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.DeliveryOrder;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.Role;
import com.example.deliveryService.domain.RoleEnum;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.repository.DeliveryOrderRepository;
import com.example.deliveryService.repository.DeliveryPersonnelRepository;
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
public class DeliveryPersonnelService {

	@Autowired
	private DeliveryPersonnelRepository deliveryPersonnelRepository;

	@Autowired
	private DeliveryOrderRepository deliveryOrderRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTService jwtService;

	public DeliveryPersonnel findDeliveryPersonnelByUsername(String username) {
		return deliveryPersonnelRepository.findByUsername(username);
	}

	public List<DeliveryOrder> viewAvailableDeliveries(Long deliveryPersonnelId) {
		// Returns available deliveries (status = "Picked up")
		return deliveryOrderRepository.findByDeliveryPersonnelIdAndStatus(deliveryPersonnelId, "PENDING");
	}

	public DeliveryOrder acceptDelivery(Long deliveryOrderId, Long deliveryPersonnelId) {
		// Fetch the delivery order by ID
		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
				.orElseThrow(() -> new RuntimeException("Delivery Order not found"));

		// Fetch the existing DeliveryPersonnel by ID
		DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findById(deliveryPersonnelId)
				.orElseThrow(() -> new RuntimeException("Delivery Personnel not found"));

		// Set the DeliveryPersonnel on the DeliveryOrder
		deliveryOrder.setDeliveryPersonnel(deliveryPersonnel);

		// Update the delivery status to "En route"
		deliveryOrder.setStatus("En route");

		// Save the updated DeliveryOrder to the database
		return deliveryOrderRepository.save(deliveryOrder);
	}

	public void updateDeliveryStatus(Long deliveryOrderId, String status) {
		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
				.orElseThrow(() -> new RuntimeException("Delivery Order not found"));
		deliveryOrder.setStatus(status); // e.g., "Delivered"
		deliveryOrderRepository.save(deliveryOrder);
	}

	public void setDeliveryPersonnelAvailability(Long deliveryPersonnelId, boolean available) {
		DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findById(deliveryPersonnelId)
				.orElseThrow(() -> new RuntimeException("Delivery Personnel not found"));
		deliveryPersonnel.setAvailable(available);
		deliveryPersonnelRepository.save(deliveryPersonnel);
	}

	public boolean isUsernameAvailable(String username) {
		// Check if a customer with the given username exists
		DeliveryPersonnel existingDeliveryPersonnel = deliveryPersonnelRepository.findByUsername(username);

		// If a customer is found with the given username, return false (username not
		// available)
		return existingDeliveryPersonnel != null;
	}

	public DeliveryPersonnel registerDeliveryPersonnel(DeliveryPersonnel deliveryPersonnel) {

		// Create a Role entity for the customer
		Role deliveryPersonnelRole = roleRepository.findByRoleName(RoleEnum.ROLE_DELIVERY_PERSONNEL.toString());

		if (deliveryPersonnelRole == null) {
			// If the role doesn't exist in the DB, you could optionally create a new Role
			// entity and save it
			deliveryPersonnelRole = new Role(RoleEnum.ROLE_DELIVERY_PERSONNEL.toString());
			roleRepository.save(deliveryPersonnelRole);
		}

		// Assign the role to the customer
		deliveryPersonnel.setAvailable(true);
		deliveryPersonnel.setRoles(List.of(deliveryPersonnelRole)); // Assign the role entity

		return deliveryPersonnelRepository.save(deliveryPersonnel);
	}

	public LoginResponse authenticateDeliveryPersonnel(String username, String password) {
		// Authenticate using the authentication manager
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Fetch customer from the database by username
		DeliveryPersonnel authenticatedDeliveryPersonnel = deliveryPersonnelRepository.findByUsername(username);

		// Extract roles from the authenticated customer
		List<String> roles = authenticatedDeliveryPersonnel.getRoles().stream().map(role -> role.getRoleName())
				.collect(Collectors.toList());

		// Generate JWT token including roles
		String token = jwtService.generateToken(username, roles);

		// Return LoginResponse with username, token, and roles
		return new LoginResponse(authenticatedDeliveryPersonnel.getUsername(), token, roles);
	}
}
