package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.DayOfService;
import com.turnosmedicos.turnosmedicos.services.DayOfServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/day-of-service")
@RestController
public class DayOfServiceController {

    private final DayOfServiceService dayOfServiceService;

    @Autowired
    public DayOfServiceController (DayOfServiceService dayOfServiceService) {
        this.dayOfServiceService = dayOfServiceService;
    }

    @PostMapping("{id}")
    public ResponseEntity<?> addDayOfServiceServiceToDoctor (@RequestHeader(value = "Authorization")String token, @RequestBody DayOfService dayOfService, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(dayOfServiceService.addDayOfServiceServiceToDoctor(token, dayOfService, id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor does not exist");
        }  catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }
}
