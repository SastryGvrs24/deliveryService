package com.example.deliveryService.service;

import com.example.deliveryService.domain.*;
import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.dto.UserDTO;
import com.example.deliveryService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdministratorService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;
    @Autowired
    private DeliveryPersonnelRepository deliveryPersonnelRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ItemOrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	@Lazy
	private AuthenticationManager authenticationManager;

	
	@Autowired
	private RoleRepository roleRepository;
	
	public LoginResponse authenticateAdmin(String username, String password) {
		// Authenticate using the authentication manager
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Fetch customer from the database by username
		Admin authenticatedAdmin = adminRepository.findByUsername(username);

		// Extract roles from the authenticated customer
		List<String> roles = authenticatedAdmin.getRoles().stream().map(role -> role.getRoleName())
				.collect(Collectors.toList());

		// Generate JWT token including roles
		String token = jwtService.generateToken(username, roles);

		// Return LoginResponse with username, token, and roles
		return new LoginResponse(authenticatedAdmin.getUsername(), token, roles);
	}
	
	public boolean isUsernameAvailable(String username) {
		// Check if a customer with the given username exists
		Admin existingAdmin = adminRepository.findByUsername(username);

		// If a customer is found with the given username, return false (username not
		// available)
		return existingAdmin != null;
	}
	
	public Admin registerAdmin(Admin admin) {

		// Create a Role entity for the customer
		Role adminRole = roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN.toString());

		if (adminRole == null) {
			// If the role doesn't exist in the DB, you could optionally create a new Role
			// entity and save it
			adminRole = new Role(RoleEnum.ROLE_ADMIN.toString());
			roleRepository.save(adminRole);
		}

		// Assign the role to the customer
		admin.setRoles(List.of(adminRole)); // Assign the role entity

		return adminRepository.save(admin);
	}
	
	public AppUser createUser(UserDTO userDTO) throws Exception {
	    // Validate the input
	    if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
	        throw new IllegalArgumentException("Role must be specified");
	    }

	    // Determine the user type and create the appropriate entity
	    AppUser user;
	    switch (userDTO.getRole()) {
	        case "ROLE_CUSTOMER":
	            user = new Customer(userDTO.getUsername(), userDTO.getPassword());
	            break;
	        case "ROLE_RESTAURANT_OWNER":
	            user = new RestaurantOwner(userDTO.getUsername(), userDTO.getPassword());
	            break;
	        case "ROLE_DELIVERY_PERSONNEL":
	            user = new DeliveryPersonnel(userDTO.getUsername(), userDTO.getPassword());
	            break;
	        case "ROLE_ADMIN":
	            user = new Admin(userDTO.getUsername(), userDTO.getPassword());
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid user type specified");
	    }

	    // Encrypt the password before saving
	    user.setPassword(passwordEncoder.encode(user.getPassword()));

	    // Fetch or create the Role entity
	    Role userRole = roleRepository.findByRoleName(userDTO.getRole());
	    if (userRole == null) {
	        userRole = new Role();
	        userRole.setRoleName(userDTO.getRole());
	        userRole = roleRepository.save(userRole); // Save the role first
	    }

	    // Assign the role to the user
	    user.setRoles(List.of(userRole));

	    // Save the user to the appropriate repository
	    if (user instanceof Customer) {
	        return customerRepository.save((Customer) user);
	    } else if (user instanceof RestaurantOwner) {
	        return restaurantOwnerRepository.save((RestaurantOwner) user);
	    } else if (user instanceof DeliveryPersonnel) {
	        return deliveryPersonnelRepository.save((DeliveryPersonnel) user);
	    } else if (user instanceof Admin) {
	        return adminRepository.save((Admin) user);
	    } else {
	        throw new IllegalStateException("Unexpected user type");
	    }
	}



	   public AppUser updateUser(Long userId, UserDTO userDTO) throws Exception {
		    // Validate the input
		    if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
		        throw new IllegalArgumentException("Role must be specified");
		    }

		    String role = userDTO.getRole(); // Extract the role
		    AppUser updatedUser;

		    switch (role) {
		        case "ROLE_CUSTOMER":
		            Customer customer = customerRepository.findById(userId)
		                    .orElseThrow(() -> new Exception("User not found"));

		            // Update general properties
		            customer.setUsername(userDTO.getUsername());
		            customer.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt password

		            // Update customer-specific properties
		            if (userDTO.getDeliveryAddress() != null) {
		                customer.setDeliveryAddress(userDTO.getDeliveryAddress());
		            }
		            if (userDTO.getPaymentDetails() != null) {
		                customer.setPaymentDetails(userDTO.getPaymentDetails());
		            }

		            updatedUser = customerRepository.save(customer);
		            break;

		        case "ROLE_RESTAURANT_OWNER":
		            RestaurantOwner restaurantOwner = restaurantOwnerRepository.findById(userId)
		                    .orElseThrow(() -> new Exception("User not found"));

		            // Update general properties
		            restaurantOwner.setUsername(userDTO.getUsername());
		            restaurantOwner.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt password

		            // Update restaurant owner-specific properties
		            if (userDTO.getRestaurantName() != null) {
		                restaurantOwner.setRestaurantName(userDTO.getRestaurantName());
		            }
		            if (userDTO.getAddress() != null) {
		                restaurantOwner.setAddress(userDTO.getAddress());
		            }
		            if (userDTO.getHoursOfOperation() != null) {
		                restaurantOwner.setHoursOfOperation(userDTO.getHoursOfOperation());
		            }

		            updatedUser = restaurantOwnerRepository.save(restaurantOwner);
		            break;

		        case "ROLE_DELIVERY_PERSONNEL":
		            DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findById(userId)
		                    .orElseThrow(() -> new Exception("User not found"));

		            // Update general properties
		            deliveryPersonnel.setUsername(userDTO.getUsername());
		            deliveryPersonnel.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt password

		            // Update delivery personnel-specific properties
		            if (userDTO.getContactDetails() != null) {
		                deliveryPersonnel.setContactDetails(userDTO.getContactDetails());
		            }
		            if (userDTO.getVehicleType() != null) {
		                deliveryPersonnel.setVehicleType(userDTO.getVehicleType());
		            }

		            updatedUser = deliveryPersonnelRepository.save(deliveryPersonnel);
		            break;

		        case "ROLE_ADMIN":
		            Admin admin = adminRepository.findById(userId)
		                    .orElseThrow(() -> new Exception("User not found"));

		            // Update general properties
		            admin.setUsername(userDTO.getUsername());
		            admin.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encrypt password

		            // Update admin-specific properties
		            if (userDTO.getAdminLevel() != null) {
		                admin.setAdminLevel(userDTO.getAdminLevel());
		            }

		            updatedUser = adminRepository.save(admin);
		            break;

		        default:
		            throw new IllegalArgumentException("Invalid role specified");
		    }

		    return updatedUser;
		}

    public void deactivateUser(Long userId) throws Exception {
        Optional<AppUser> user = userRepository.findById(userId); // Use the base User repository to fetch any user
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new Exception("User not found");
        }
    }



    // View All Orders
    public List<item_Order> viewAllOrders() {
        return orderRepository.findAll();
    }

    // Cancel Order
    public void cancelOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }


    public Map<String, Object> generateReport() {
        // Total orders
        long totalOrders = orderRepository.count();

        // Orders grouped by status
        List<Object[]> statusCounts = orderRepository.countOrdersByStatus();
        Map<String, Long> statusReport = new HashMap<>();
        for (Object[] statusCount : statusCounts) {
            statusReport.put((String) statusCount[0], (Long) statusCount[1]);
        }

        // Orders grouped by restaurant
        List<Object[]> restaurantOrderCounts = orderRepository.countOrdersByRestaurant();
        Map<String, Long> restaurantReport = new HashMap<>();
        for (Object[] restaurantCount : restaurantOrderCounts) {
            restaurantReport.put((String) restaurantCount[0], (Long) restaurantCount[1]);
        }

        // Construct the full report in JSON format
        Map<String, Object> report = new HashMap<>();
        report.put("totalOrders", totalOrders);
        report.put("orderStatusCounts", statusReport);
        report.put("ordersPerRestaurant", restaurantReport);

        return report;
    }
    // Monitor Platform Activity
    public Map<String, Object> monitorPlatformActivity() {
        // Count users by role
        long totalCustomers = userRepository.countByRole("ROLE_CUSTOMER");
        long totalRestaurantOwners = userRepository.countByRole("ROLE_RESTAURANT_OWNER");
        long totalDeliveryPersonnel = userRepository.countByRole("ROLE_DELIVERY_PERSONNEL");
        long totalAdmins = userRepository.countByRole("ROLE_ADMIN");
        
        // Count orders by status (e.g., PENDING, DELIVERED, etc.)
        long totalPendingOrders = orderRepository.countByStatus("PENDING");
        long totalDeliveredOrders = orderRepository.countByStatus("DELIVERED");
        

        // Construct the platform activity report in JSON format
        Map<String, Object> activityReport = new HashMap<>();
        Map<String, Long> userRoleCounts = new HashMap<>();
        userRoleCounts.put("Customers", totalCustomers);
        userRoleCounts.put("Restaurant Owners", totalRestaurantOwners);
        userRoleCounts.put("Delivery Personnel", totalDeliveryPersonnel);
        userRoleCounts.put("Admins", totalAdmins);

        Map<String, Long> orderStatusCounts = new HashMap<>();
        orderStatusCounts.put("Pending Orders", totalPendingOrders);
        orderStatusCounts.put("Delivered Orders", totalDeliveredOrders);

        // Putting the data into the final map
        activityReport.put("totalUsersByRole", userRoleCounts);
        activityReport.put("totalOrdersByStatus", orderStatusCounts);

        return activityReport;
    }
}
