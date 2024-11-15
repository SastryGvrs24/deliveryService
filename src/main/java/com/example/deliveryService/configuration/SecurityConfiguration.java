package com.example.deliveryService.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.deliveryService.service.CustomAuthenticationProvider;
import com.example.deliveryService.service.DAOCustomerDetailService;
import com.example.deliveryService.service.DAORestaurantDetailService;

@Configuration
@EnableWebSecurity
@CrossOrigin
public class SecurityConfiguration {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private DAOCustomerDetailService customerDetailService; // Customer service

	@Autowired
	@Qualifier("restaurantDetailService") // Qualifier for restaurant service
	private DAORestaurantDetailService restaurantDetailService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Removed the direct injection of CustomAuthenticationProvider here

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.authorizeHttpRequests(request -> request
						// Public endpoints for both customers and restaurant owners
						.requestMatchers("/**", "/api/customer/signup", "/api/customer/login",
								"/api/customer/checkUsernameAvailability", "/h2-console/**")
						.permitAll() // These are accessible without authentication

						// Customer specific endpoints that require ROLE_CUSTOMER
						.requestMatchers("/api/customer/update", "/api/customer/Order", "/api/customer/orders/**",
								"/api/customer/searchMenuItems")
						.hasRole("CUSTOMER") // Ensure that only users with 'ROLE_CUSTOMER' can access these endpoints

						// Restaurant Owner specific endpoints that require ROLE_RESTAURANT_OWNER
						.requestMatchers("/api/restaurant/signup", "/api/restaurant/login",
								"/api/restaurant/checkUsernameAvailability")
						.permitAll() // These endpoints should be available without authentication (for sign-up and
										// login)

						.requestMatchers("/api/restaurant/menu/**", "/api/restaurant/menu", "/api/restaurant/update",
								"/api/restaurant/orders", "/api/restaurant/orders/**")
						.hasRole("RESTAURANT_OWNER") // Ensure that only users with 'ROLE_RESTAURANT_OWNER' can access
														// restaurant management endpoints

						// For Delivery Personnel
						.requestMatchers("/api/deliveryPersonnel/signup", "/api/deliveryPersonnel/login",
								"/api/deliveryPersonnel/checkUsernameAvailability")
						.permitAll() // These endpoints should be available without authentication (for sign-up and
										// login)
						
						.anyRequest().authenticated() // All other requests require authentication
				).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
																												// authentication
																												// for
																												// JWT
				.build();

	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
			CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
				.authenticationProvider(customAuthenticationProvider) // Use the custom provider
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
