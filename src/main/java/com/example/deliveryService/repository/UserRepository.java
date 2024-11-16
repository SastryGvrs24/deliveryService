package com.example.deliveryService.repository;

import com.example.deliveryService.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    
    @Query("SELECT COUNT(u) FROM AppUser u JOIN u.roles r WHERE r.roleName = :role")
    long countByRole(@Param("role") String role);

}
