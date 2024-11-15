package com.example.deliveryService.domain;

import java.util.List;

import jakarta.persistence.*;
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName; // Role name, like "ROLE_CUSTOMER", "ROLE_ADMIN", etc.

    @ManyToMany(mappedBy = "roles")
    private List<Customer> customers;
    
    @ManyToMany(mappedBy = "roles")
    private List<RestaurantOwner> restaurantOwners;
    
    @ManyToMany(mappedBy = "roles")
    private List<DeliveryPersonnel> deliveryPersonnel;


	// Default constructor
    public Role() {}

    // Constructor
    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
    
    public List<RestaurantOwner> getRestaurantOwners() {
		return restaurantOwners;
	}

	public void setRestaurantOwners(List<RestaurantOwner> restaurantOwners) {
		this.restaurantOwners = restaurantOwners;
	}

}

