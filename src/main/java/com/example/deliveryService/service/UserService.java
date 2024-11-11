package com.example.deliveryService.service;

import com.example.deliveryService.dto.LoginResponse;
import com.example.deliveryService.domain.User;
import com.example.deliveryService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService() {
    }

    public User signUp(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public LoginResponse login(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),  user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(token);
    }

    public boolean isUsernameAvailable(String userName) {
       User user = userRepository.findByUsername(userName);

        if(user != null) {
            return true;
        } else {
            return false;
        }
    }


    public boolean updateUser(User user) {
        User userdetails = userRepository.findByUsername(user.getUsername());

        if(userdetails != null) {
            userdetails.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.saveAndFlush(user);
            return true;
        } else {
            return false;
        }
    }

}
