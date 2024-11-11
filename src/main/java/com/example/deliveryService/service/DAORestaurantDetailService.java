
package com.example.deliveryService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.deliveryService.domain.RestaurantDetailsImpl;
import com.example.deliveryService.domain.RestaurantOwner;
import com.example.deliveryService.repository.RestaurantOwnerRepository;

@Service
@Qualifier("restaurantDetailService")
public class DAORestaurantDetailService implements UserDetailsService {

    @Autowired
    RestaurantOwnerRepository restaurantOwnerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RestaurantOwner restaurantOwner = restaurantOwnerRepository.findByUsername(username);

        if (restaurantOwner == null) {
            throw new UsernameNotFoundException("Restaurant not in db");
        }

        return new RestaurantDetailsImpl(restaurantOwner);
    }
}