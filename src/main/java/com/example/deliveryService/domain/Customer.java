package com.example.deliveryService.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String password;

	private String fullName;
	private String deliveryAddress;
	private String paymentDetails; // You can store this in an encrypted format for security reasons

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private List<item_Order> orders;

    @ManyToMany
    @JoinTable(
        name = "customer_roles",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

	public Customer() {
		// Default constructor for JPA
	}

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

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
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

	// Constructor
	public Customer(String username, String password, String fullName, String deliveryAddress, String paymentDetails) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.deliveryAddress = deliveryAddress;
		this.paymentDetails = paymentDetails;
	}
}
