package com.example.deliveryService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliveryService.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Find a Customer by their username
    Admin findByUsername(String username);

}
