package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.services.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/shift")
@RestController
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping
    public ResponseEntity<?> getAllShift(@RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(shiftService)
        }
    }
}
