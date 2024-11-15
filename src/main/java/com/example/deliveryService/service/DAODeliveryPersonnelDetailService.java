package com.example.deliveryService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.CustomerDetailsImpl;
import com.example.deliveryService.domain.DeliveryPersonnel;
import com.example.deliveryService.domain.DeliveryPersonnelDetailsImpl;
import com.example.deliveryService.repository.CustomerRepository;
import com.example.deliveryService.repository.DeliveryPersonnelRepository;

@Service
@Qualifier("deiveryPersonnelDetailService")
public class DAODeliveryPersonnelDetailService implements UserDetailsService  {

    @Autowired
    DeliveryPersonnelRepository deliveryPersonnelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DeliveryPersonnel deliveryPersonnel = deliveryPersonnelRepository.findByUsername(username);

        if (deliveryPersonnel == null) {
            throw new UsernameNotFoundException("User not in db");
        }

        return new DeliveryPersonnelDetailsImpl(deliveryPersonnel);
    }
}
