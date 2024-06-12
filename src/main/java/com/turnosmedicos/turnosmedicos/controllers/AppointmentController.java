package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.exceptions.AppointmentAlreadyExistingException;
import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.exceptions.UserMismatchException;
import com.turnosmedicos.turnosmedicos.models.Appointment;
import com.turnosmedicos.turnosmedicos.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/appointment")
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointment(@RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(appointmentService.getAllAppointment(token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getDayAppointmentAvailable(@PathVariable Long id, @RequestHeader(value = "Authorization")String token, @RequestParam Integer dayNumber) {
        try {
            return ResponseEntity.ok(appointmentService.getAllAppointmentAvailable(token, id, dayNumber));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @GetMapping("next-day/{id}")
    public ResponseEntity<?> getNextDayAvailable(@PathVariable Long id, @RequestHeader(value = "Authorization")String token, @RequestParam Integer dayNumber) {
        try {
            return ResponseEntity.ok(appointmentService.getNextDayAvailable(token, id, dayNumber));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @GetMapping("previous-day/{id}")
    public ResponseEntity<?> getPreviousDayAvailable(@PathVariable Long id, @RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(appointmentService.getPreviousDayAvailable(token, id));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        }
    }

    @PostMapping
    public ResponseEntity<?> addAppointment(@RequestHeader(value = "Authorization")String token, @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.addAppointment(token, appointment));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        } catch (AppointmentAlreadyExistingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The appointment already existing");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(appointmentService.cancelAppointment(id, token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: invalid token");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment or user does not exist");
        } catch (UserMismatchException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Mismatch");
        }
    }
}
