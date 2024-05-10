package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.Doctor;
import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.repositories.DoctorRepository;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import com.turnosmedicos.turnosmedicos.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, JWTUtil jwtUtil) {
        this.doctorRepository = doctorRepository;
        this.jwtUtil = jwtUtil;
    }

    public Doctor addUser(Doctor doctor) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(1, 1024, 1, doctor.getPassword());
        doctor.setPassword(hash);

        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorInfo(String token) {
        String userId = jwtUtil.getKey(token);
        if(userId!=null){
            return doctorRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new ResourceNotFoundException("The user with this id: " + userId + "is not found"));
        } throw new UnauthorizedException("invalid token");
    }
}