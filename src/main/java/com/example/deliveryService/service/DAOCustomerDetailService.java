
package com.example.deliveryService.service;

import com.example.deliveryService.domain.Customer;
import com.example.deliveryService.domain.CustomerDetailsImpl;
import com.example.deliveryService.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@Qualifier("customerDetailService")
public class DAOCustomerDetailService implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer user = customerRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not in db");
        }

        return new CustomerDetailsImpl(user);
    }
}