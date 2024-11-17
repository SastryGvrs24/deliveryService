package com.example.deliveryService.service;

import com.example.deliveryService.domain.Admin;
import com.example.deliveryService.domain.AdminDetailsImpl;
import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.CustomerDetailsImpl;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.DeliveryPersonnelDetailsImpl;
import com.example.deliveryService.domain.RestaurantDetailsImpl;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.repository.AdminRepository;
import com.example.deliveryService.repository.CustomerRepository;
import com.example.deliveryService.repository.DeliveryPersonnelRepository;
import com.example.deliveryService.repository.RestaurantOwnerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final DeliveryPersonnelRepository deliveryPersonnelRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for PasswordEncoder
    public CustomAuthenticationProvider(CustomerRepository customerRepository,
                                        RestaurantOwnerRepository restaurantOwnerRepository,
                                        DeliveryPersonnelRepository deliveryPersonnelRepository,
                                        AdminRepository adminRepository,
                                        PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.deliveryPersonnelRepository = deliveryPersonnelRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // First, try to authenticate as a Customer
        try {
            UserDetails customerDetails = loadCustomerByUsername(username);
            if (passwordEncoder.matches(password, customerDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(customerDetails, password, customerDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
            // If customer not found, try RestaurantOwner
        }

        // Second, try to authenticate as a RestaurantOwner
        try {
            UserDetails restaurantOwnerDetails = loadRestaurantOwnerByUsername(username);
            if (passwordEncoder.matches(password, restaurantOwnerDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(restaurantOwnerDetails, password, restaurantOwnerDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
        	// If restaurant not found, try DeliveryPersonnel
        }

     // Third, try to authenticate as a DeliveryPersonnel
        try {
            UserDetails deliveryPersonnelDetails = loadDeliveryPersonnelByUsername(username);
            if (passwordEncoder.matches(password, deliveryPersonnelDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(deliveryPersonnelDetails, password, deliveryPersonnelDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
        }
        
        // Fourth, try to authenticate as a Admin
        try {
            UserDetails adminDetails = loadAdminByUsername(username);
            if (passwordEncoder.matches(password, adminDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(adminDetails, password, adminDetails.getAuthorities());
            }
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return null; // Authentication failed
    }

    private UserDetails loadCustomerByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("Customer not found with username: " + username);
        }
        return new CustomerDetailsImpl(customer); // Custom UserDetails for Customer
    }

    private UserDetails loadRestaurantOwnerByUsername(String username) throws UsernameNotFoundException {
        RestaurantOwner restaurantOwner = restaurantOwnerRepository.findByUsername(username);
        if (restaurantOwner == null) {
            throw new UsernameNotFoundException("Restaurant owner not found with username: " + username);
        }
        return new RestaurantDetailsImpl(restaurantOwner); // Custom UserDetails for RestaurantOwner
    }
    
    private UserDetails loadDeliveryPersonnelByUsername(String username) throws UsernameNotFoundException {
        DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findByUsername(username);
        if (deliveryPersonnel == null) {
            throw new UsernameNotFoundException("Delivery personnel not found with username: " + username);
        }
        return new DeliveryPersonnelDetailsImpl(deliveryPersonnel); // Custom UserDetails for DeliveryPersonnel
    }
    
    private UserDetails loadAdminByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("Admin not found with username: " + username);
        }
        return new AdminDetailsImpl(admin); // Custom UserDetails for DeliveryPersonnel
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
