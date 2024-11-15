package com.example.deliveryService.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DeliveryPersonnelDetailsImpl implements UserDetails{
	DeliveryPersonnel deliveryPersonnel;

	public DeliveryPersonnelDetailsImpl(DeliveryPersonnel deliveryPersonnel) {
        this.deliveryPersonnel = deliveryPersonnel;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return deliveryPersonnel.getPassword();
	}

	@Override
	public String getUsername() {
		return deliveryPersonnel.getUsername();
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


