
package com.example.deliveryService.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class RestaurantDetailsImpl implements UserDetails {

	RestaurantOwner restaurantOwner;

	public RestaurantDetailsImpl(RestaurantOwner restaurantOwner) {
        this.restaurantOwner = restaurantOwner;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return restaurantOwner.getPassword();
	}

	@Override
	public String getUsername() {
		return restaurantOwner.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}