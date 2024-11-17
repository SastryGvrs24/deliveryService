package com.example.deliveryService.service;

import com.example.deliveryService.domain.Role;
import com.example.deliveryService.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

	@Autowired
	private RoleRepository rolesRepository;

	public Role getRoleByName(String roleName) {
		return rolesRepository.findByRoleName(roleName); // Assuming Roles repository exists
	}
}
