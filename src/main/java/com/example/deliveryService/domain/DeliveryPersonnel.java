package com.example.deliveryService.domain;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class DeliveryPersonnel {

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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String password;
	private String fullName;
	private String contactDetails;
	private String vehicleType;
	private boolean available; // Status can be "AVAILABLE", "UNAVAILABLE"

	@OneToMany(mappedBy = "deliveryPersonnel")
	private List<DeliveryOrder> deliveryOrders;

	@ManyToMany
	@JoinTable(name = "delivery_personnel_roles", joinColumns = @JoinColumn(name = "delivery_personnel_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	public boolean isAvailable() {
		return available;
	}

	// Constructor, Getters, Setters
	public DeliveryPersonnel() {
	}

	public DeliveryPersonnel(String name, String contactDetails, String vehicleType, boolean status) {
		this.username = name;
		this.contactDetails = contactDetails;
		this.vehicleType = vehicleType;
		this.available = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return username;
	}

	public void setName(String name) {
		this.username = name;
	}

	public String getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public boolean getStatus() {
		return available;
	}

	public void setStatus(boolean status) {
		this.available = status;
	}

	public List<DeliveryOrder> getDeliveryOrders() {
		return deliveryOrders;
	}

	public void setDeliveryOrders(List<DeliveryOrder> deliveryOrders) {
		this.deliveryOrders = deliveryOrders;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
