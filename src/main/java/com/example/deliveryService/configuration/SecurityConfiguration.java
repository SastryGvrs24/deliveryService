package com.example.deliveryService.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .authorizeHttpRequests(request -> request
            // Public endpoints for both customers and restaurant owners
            .requestMatchers("/**", "/api/customer/signup", "/api/customer/login", "/api/customer/checkUsernameAvailability", "/h2-console/**")
                .permitAll()  // These are accessible without authentication
            
            // Customer specific endpoints that require ROLE_CUSTOMER
            .requestMatchers("/api/customer/update", "/api/customer/placeOrder", "/api/customer/orders/**")
                .hasRole("CUSTOMER")  // Ensure that only users with 'ROLE_CUSTOMER' can access these endpoints

            // Restaurant Owner specific endpoints that require ROLE_RESTAURANT_OWNER
            .requestMatchers("/api/restaurant/signup", "/api/restaurant/login", "/api/restaurant/checkUsernameAvailability")
                .permitAll()  // These endpoints should be available without authentication (for sign-up and login)

            .requestMatchers("/api/restaurant/menu/**", "/api/restaurant/update")
                .hasRole("RESTAURANT_OWNER")  // Ensure that only users with 'ROLE_RESTAURANT_OWNER' can access restaurant management endpoints
            
            .anyRequest().authenticated()  // All other requests require authentication
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless authentication for JWT
        .build();

	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}