package com.example.deliveryService.repository;

import com.example.deliveryService.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find a Customer by their username
    Customer findByUsername(String username);

}
