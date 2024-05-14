package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/login")
@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> validationCredentials(@RequestBody User user) {
        try {
            return ResponseEntity.ok(authService.validationCredentials(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dni or password is incorrect");
        }
    }
}
