package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.Doctor;
import com.turnosmedicos.turnosmedicos.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/doctor")
@RestController
public class DoctorController {
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return doctorService.addDoctor(doctor);
    }

    @GetMapping
    public ResponseEntity<?> getDoctorInfo(@RequestParam String token) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorInfo(token));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }
    }

    @GetMapping("bySpeciality/{speciality}")
    public ResponseEntity<?> getDoctorBySpeciality(@RequestHeader(value = "Authorization")String token, @PathVariable String speciality) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorBySpeciality(token, speciality));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getDoctor(@RequestHeader(value = "Authorization")String token, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(doctorService.getDoctor(token, id));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid token");
        }
    }
}
