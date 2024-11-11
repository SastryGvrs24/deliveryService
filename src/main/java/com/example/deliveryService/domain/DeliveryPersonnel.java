package com.example.deliveryService.domain;

import jakarta.persistence.*;

@Entity
public class DeliveryPersonnel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String password;
	private String fullName;
	private String contactDetails;

	public DeliveryPersonnel() {
	}

	// Constructor with id (for initializing only the id)
	public DeliveryPersonnel(Long id) {
		this.id = id;
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

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	private String vehicleType;
	private boolean available; // To indicate if the delivery person is available for deliveries

	public DeliveryPersonnel(String username, String password, String fullName, String contactDetails,
			String vehicleType) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.contactDetails = contactDetails;
		this.vehicleType = vehicleType;
		this.available = true; // Default availability is true
	}

	// Getters and Setters
}
