package com.example.deliveryService.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class RestaurantOwner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String password;
	private String restaurantName;
	private String address;
	private String hoursOfOperation;

	@OneToMany(mappedBy = "restaurantOwner")
    private List<item_Order> orders;

	@ManyToMany
	@JoinTable(name = "restaurant_owner_roles", joinColumns = @JoinColumn(name = "restaurant_owner_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;
	
	public RestaurantOwner() {
		// Default constructor for JPA
	}

	// Getters and setters
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

	public String getAddress() {
		return address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHoursOfOperation() {
		return hoursOfOperation;
	}

	public void setHoursOfOperation(String hoursOfOperation) {
		this.hoursOfOperation = hoursOfOperation;
	}

	public List<item_Order> getOrders() {
		return orders;
	}

	public void setOrders(List<item_Order> orders) {
		this.orders = orders;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public RestaurantOwner(String username, String password, String restaurantName, String address,
			String hoursOfOperation) {
		this.username = username;
		this.password = password;
		this.restaurantName = restaurantName;
		this.address = address;
		this.hoursOfOperation = hoursOfOperation;
	}
}
