package com.turnosmedicos.turnosmedicos.controllers;

import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@CrossOrigin(origins = {"http://localhost:19006/", "192.168.0.9:8081"})
@RequestMapping("/api/history")
@RestController
public class HistoryController {
    private final HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(@RequestHeader(value = "Authorization")String token) {
        try {
            return ResponseEntity.ok(historyService.getHistory(token));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("{id}")
    public ResponseEntity<?> addHistory(@RequestHeader(value = "Authorization")String token, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(historyService.addHistory(token, id));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment does exist");
        }
    }
}
