package org.example.fooddeliverysystem.service;

import org.example.fooddeliverysystem.dto.auth.AuthResponse;
import org.example.fooddeliverysystem.dto.auth.LoginRequest;
import org.example.fooddeliverysystem.dto.auth.RegisterRequest;
import org.example.fooddeliverysystem.enums.Role;
import org.example.fooddeliverysystem.model.User;
import org.example.fooddeliverysystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        User user = new User(
            request.getPhoneNo(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getName(),
            request.getAddress(),
            request.getRole() != null ? request.getRole() : Role.USER
        );
        
        user = userRepository.save(user);
        
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getPhoneNo(),
            user.getName(),
            user.getRole()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailOrPhoneNo(request.getIdentifier(), request.getIdentifier())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }
        
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getPhoneNo(),
            user.getName(),
            user.getRole()
        );
    }
}
