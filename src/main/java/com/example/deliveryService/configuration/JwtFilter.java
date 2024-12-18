package com.example.deliveryService.configuration;

import com.example.deliveryService.service.DAOAdminDetailService;
import com.example.deliveryService.service.DAOCustomerDetailService;
import com.example.deliveryService.service.DAODeliveryPersonnelDetailService;
import com.example.deliveryService.service.DAORestaurantDetailService;
import com.example.deliveryService.service.JWTService;
import io.jsonwebtoken.MalformedJwtException;
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
    @Qualifier("deliveryPersonnelDetailService")
    private DAODeliveryPersonnelDetailService daoDeliveryPersonnelDetailService;

    @Autowired
    @Qualifier("adminDetailService")
    private DAOAdminDetailService daoAdminDetailService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        // Skip authentication for certain endpoints
        if (requestURI.contains("v3/api-docs") || requestURI.contains("/swagger-ui") || requestURI.contains("h2-console") || requestURI.equals("/api/customer/signup")
                || requestURI.equals("/api/customer/login")
                || requestURI.contains("/api/customer/checkUsernameAvailablility")
                || requestURI.equals("/api/restaurant/signup") || requestURI.equals("/api/restaurant/login")
                || requestURI.contains("/api/restaurant/checkUsernameAvailablility")
                || requestURI.equals("/api/deliveryPersonnel/signup")
                || requestURI.equals("/api/deliveryPersonnel/login")
                || requestURI.contains("/api/deliveryPersonnel/checkUsernameAvailablility") || requestURI.equals("/api/admin/signup")
                || requestURI.equals("/api/admin/login")
                || requestURI.contains("/api/admin/checkUsernameAvailablility")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract and validate JWT token
        String token = request.getHeader("Authorization");
        String jwtToken = null;
        String userName = null;

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7).trim();
            try {
                userName = jwtService.extractUserName(jwtToken); // Extract username from JWT
            } catch (MalformedJwtException e) {
                // Handle invalid JWT format
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token format");
                return;
            } catch (Exception e) {
                // Handle other exceptions that may occur during JWT extraction
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized access: " + e.getMessage());
                return;
            }
        } else {
            // If the token is missing, return Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization token is missing");
            return;
        }

        // Authenticate only if username exists and no existing auth in the context
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Determine user role from JWT
            String role = extractUserRole(jwtToken);
            UserDetailsService userDetailsService = getUserDetailsServiceByRole(role);

            // Authenticate if a valid role-based UserDetailsService is found
            if (userDetailsService != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (userDetails == null) {
                    logger.warn("UserDetails for username: " + userName + " is null.");
                } else {
                    logger.debug("UserDetails loaded successfully for username: " + userName);
                }

                if (jwtService.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("JWT validation failed for username: " + userName);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid JWT token");
                    return;
                }
            } else {
                logger.warn("No matching UserDetailsService found for role: " + role);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized access");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // Extracts role from JWT token (modify as needed based on JWTService)
    private String extractUserRole(String jwtToken) {
        if (jwtService.hasRole(jwtToken, "ROLE_CUSTOMER")) {
            return "ROLE_CUSTOMER";
        } else if (jwtService.hasRole(jwtToken, "ROLE_RESTAURANT_OWNER")) {
            return "ROLE_RESTAURANT_OWNER";
        } else if (jwtService.hasRole(jwtToken, "ROLE_DELIVERY_PERSONNEL")) {
            return "ROLE_DELIVERY_PERSONNEL";
        } else if (jwtService.hasRole(jwtToken, "ROLE_ADMIN")) {
            return "ROLE_ADMIN";
        }
        // Add other roles as needed
        return null;
    }

    // Returns the appropriate UserDetailsService based on the role
    private UserDetailsService getUserDetailsServiceByRole(String role) {
        switch (role) {
            case "ROLE_CUSTOMER":
                return daoCustomerDetailService;
            case "ROLE_RESTAURANT_OWNER":
                return daoRestaurantDetailService;
            case "ROLE_DELIVERY_PERSONNEL":
                return daoDeliveryPersonnelDetailService;
            case "ROLE_ADMIN":
                return daoAdminDetailService;
            // Add cases for additional roles with their respective UserDetailsService
            default:
                return null;
        }
    }
}
