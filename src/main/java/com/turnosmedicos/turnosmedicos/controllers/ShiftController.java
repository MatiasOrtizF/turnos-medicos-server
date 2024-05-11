package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.services.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
            return ResponseEntity.ok(shiftService.getAllShift(token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @PostMapping
    public ResponseEntity<?> addShift(@RequestHeader(value = "Authorization")String token, @RequestParam Date date, @RequestParam Long doctorId) {
        try {
            return ResponseEntity.ok(shiftService.addShift(date, doctorId, token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> cancelShift(@PathVariable Long id, @RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(shiftService.cancelShift(id, token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }
}
