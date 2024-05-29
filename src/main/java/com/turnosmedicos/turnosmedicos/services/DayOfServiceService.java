package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.DayOfService;
import com.turnosmedicos.turnosmedicos.models.Doctor;
import com.turnosmedicos.turnosmedicos.repositories.DayOfServiceRepository;
import com.turnosmedicos.turnosmedicos.repositories.DoctorRepository;
import com.turnosmedicos.turnosmedicos.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DayOfServiceService {
    private final DayOfServiceRepository dayOfServiceRepository;
    private final DoctorRepository doctorRepository;
    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public DayOfServiceService (DayOfServiceRepository dayOfServiceRepository, DoctorRepository doctorRepository, JWTUtil jwtUtil, AuthService authService) {
        this.dayOfServiceRepository = dayOfServiceRepository;
        this. doctorRepository = doctorRepository;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    public DayOfService addDayOfServiceServiceToDoctor(String token, DayOfService dayOfService, Long id) {
        if(authService.validationToken(token)) {
            Doctor doctor = doctorRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("The user with this id: " + id + "is not found"));
            dayOfService.setDoctor(doctor);

            return dayOfServiceRepository.save(dayOfService);
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

}
