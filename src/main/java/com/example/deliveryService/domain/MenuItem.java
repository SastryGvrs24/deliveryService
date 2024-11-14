package com.example.deliveryService.domain;

import jakarta.persistence.*;

@Entity
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;
	private Double price;
	private boolean available; // Status can be "AVAILABLE", "UNAVAILABLE"

	@ManyToOne
	@JoinColumn(name = "restaurant_id")
	private RestaurantOwner restaurant;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private item_Order order; // This is where the relationship is mapped

	private String cuisineType;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public RestaurantOwner getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(RestaurantOwner restaurant) {
		this.restaurant = restaurant;
	}

	public String getCuisineType() {
		return cuisineType;
	}

	public void setCuisineType(String cuisineType) {
		this.cuisineType = cuisineType;
	}

	public item_Order getOrder() {
		return order;
	}

	public void setOrder(item_Order order) {
		this.order = order;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}
