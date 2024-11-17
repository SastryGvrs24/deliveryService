package com.example.deliveryService.domain;

import jakarta.persistence.Entity;

@Entity
public class  Admin extends AppUser {

    private String adminLevel;

    public Admin() {
        // Default constructor for JPA
    }

    public Admin(String username, String password, String fullName, String adminLevel) {
        super(username, password, fullName); // Call the constructor of the superclass
        this.adminLevel = adminLevel;
    }
    
    public Admin(String username, String password) {
        super(username, password);
    }

    // Getter and Setter for adminLevel
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
}




