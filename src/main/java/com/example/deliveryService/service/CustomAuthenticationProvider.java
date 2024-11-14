package com.example.deliveryService.service;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.CustomerDetailsImpl;
import com.example.deliveryService.domain.RestaurantDetailsImpl;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.repository.CustomerRepository;
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
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for PasswordEncoder
    public CustomAuthenticationProvider(CustomerRepository customerRepository,
                                        RestaurantOwnerRepository restaurantOwnerRepository,
                                        PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
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

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}