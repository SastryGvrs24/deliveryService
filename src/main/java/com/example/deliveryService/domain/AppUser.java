package com.example.deliveryService.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Use JOINED strategy for table inheritance
public abstract class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String fullName;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    public AppUser() {
        // Default constructor for JPA
    }

    public AppUser(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }
    
    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
