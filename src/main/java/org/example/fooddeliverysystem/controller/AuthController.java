package org.example.fooddeliverysystem.controller;

import jakarta.validation.Valid;
import org.example.fooddeliverysystem.dto.auth.AuthResponse;
import org.example.fooddeliverysystem.dto.auth.LoginRequest;
import org.example.fooddeliverysystem.dto.auth.RegisterRequest;
import org.example.fooddeliverysystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
