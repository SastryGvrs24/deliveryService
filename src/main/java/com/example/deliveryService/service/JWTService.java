package com.example.deliveryService.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JWTService {

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app-jwt-expiration-milliseconds}")
	private int jwtExpiration;

	public String generateToken(String userName, List<String> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles); // Add roles directly as a List to the claims map

		// Build and sign the token
		return Jwts.builder().setClaims(claims) // Set the claims
				.setSubject(userName) // Set the username as the subject
				.setIssuedAt(new Date(System.currentTimeMillis())) // Set the issued date
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Set expiration
				.signWith(getKey()) // Sign with the appropriate key
				.compact(); // Return the JWT token as a string
	}

	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String extractUserName(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		Date expirationDate = extractExpirationDate(token);

		// Check if token is expired and also check if username matches
		return userName.equals(userDetails.getUsername()) && expirationDate.after(new Date());
	}

	public Date extractExpirationDate(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload().getExpiration();
	}

	public List<String> extractRoles(String token) {
		String rolesString = (String) Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token) // Parse the
																											// token to
																											// get the
																											// claims
				.getBody().get("roles"); // Extract roles from claims

		// Convert the roles string back to a list of role names
		return List.of(rolesString.replace("[", "").replace("]", "").split(", "));
	}

	public boolean hasRole(String jwtToken, String role) {
		String rolesString = (String) Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(jwtToken).getBody().get("roles");

		return rolesString != null && rolesString.contains(role);
	}

}
