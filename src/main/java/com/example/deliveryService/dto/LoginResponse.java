package com.example.deliveryService.dto;

import java.util.List;

public class LoginResponse {
    private String username;
    private String token;
    private List<String> roles; // Add this field to store roles

    public LoginResponse(String username, String token, List<String> roles) {
        this.username = username;
        this.token = token;
        this.roles = roles;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
