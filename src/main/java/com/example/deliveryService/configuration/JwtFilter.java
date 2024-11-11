package com.example.deliveryService.configuration;

import com.example.deliveryService.service.DAOCustomerDetailService;
import com.example.deliveryService.service.DAORestaurantDetailService;
import com.example.deliveryService.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
	@Autowired
	@Qualifier("customerDetailService")
	private DAOCustomerDetailService daoCustomerDetailService;

	@Autowired
	@Qualifier("restaurantDetailService")
	private DAORestaurantDetailService daoRestaurantDetailService;
	
	@Autowired
	JWTService jwtService;

	@Autowired
	ApplicationContext applicationContext;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		// Skip authentication for certain endpoints
		if (requestURI.contains("h2-console") || requestURI.equals("/api/customer/signup")
				|| requestURI.equals("/api/customer/login")
				|| requestURI.contains("/api/customer/checkUsernameAvailablility") ||requestURI.equals("/api/restaurant/signup")
				|| requestURI.equals("/api/restaurant/login")
				|| requestURI.contains("/api/restaurant/checkUsernameAvailablility") ) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = request.getHeader("Authorization");
		String jwtToken = null;
		String userName = null;

		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			jwtToken = token.substring(7, token.length()).trim();
			userName = jwtService.extractUserName(jwtToken); // Extract userName from JWT
		}

		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// Determine if the user has any roles
			String role = null;

			// Check for customer, restaurant owner, admin, etc.
			if (jwtService.hasRole(jwtToken, "ROLE_CUSTOMER")) {
				role = "ROLE_CUSTOMER";
			} else if (jwtService.hasRole(jwtToken, "ROLE_RESTAURANT_OWNER")) {
				role = "ROLE_RESTAURANT_OWNER";
			} else if (jwtService.hasRole(jwtToken, "ROLE_ADMIN")) {
				role = "ROLE_ADMIN";
			} else if (jwtService.hasRole(jwtToken, "ROLE_DELIVERY_PERSONNEL")) {
				role = "ROLE_DELIVERY_PERSONNEL";
			}

			UserDetailsService userDetailsService = null;

			// Use a switch statement to select the appropriate UserDetailsService based on
			// the role
			switch (role) {
			case "ROLE_CUSTOMER":
				userDetailsService = daoCustomerDetailService;
				break;
			case "ROLE_RESTAURANT_OWNER":
				userDetailsService = daoRestaurantDetailService;
				break;
//                case "ROLE_ADMIN":
//                    userDetails = applicationContext.getBean(DAOAdminDetailService.class).loadUserByUsername(userName);
//                    break;
//                case "ROLE_DELIVERY_PERSONNEL":
//                    userDetails = applicationContext.getBean(DaodeliveryPersonnelDetailService.class).loadUserByUsername(userName);
//                    break;
			default:
				// If no matching role is found, handle appropriately (optional)
				logger.warn("No matching role found for the token");
				break;
			}

			if (userDetailsService != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // Correctly call loadUserByUsername() here
                if (jwtService.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        
		}

		filterChain.doFilter(request, response);
	}

}