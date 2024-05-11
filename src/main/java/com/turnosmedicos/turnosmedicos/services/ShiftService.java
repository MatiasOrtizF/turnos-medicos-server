package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.exceptions.UserMismatchException;
import com.turnosmedicos.turnosmedicos.models.Doctor;
import com.turnosmedicos.turnosmedicos.models.Shift;
import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.repositories.DoctorRepository;
import com.turnosmedicos.turnosmedicos.repositories.ShiftRepository;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShiftService {
    private final AuthService authService;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public ShiftService(AuthService authService, ShiftRepository shiftRepository, UserRepository userRepository, DoctorRepository doctorRepository) {
        this.authService = authService;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Shift> getAllShift(String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            return shiftRepository.findByUserId(userId);
        } throw new UnauthorizedException("Unauthorized: Invalid token");
    }

    public Shift addShift(Date date, Long doctorId, String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));
            Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new ResourceNotFoundException("The doctor is not found"));

            Shift newShift = new Shift();
            newShift.setUser(user);
            newShift.setDoctor(doctor);
            newShift.setDate(date);

            return shiftRepository.save(newShift);
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public Boolean cancelShift(Long id, String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("The user with this id:" + id + "is not found"));
            Shift shift = shiftRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("The shift with this id: " + id + "is not found"));;
            if(Objects.equals(user.getId(), shift.getUser().getId())) {
                shiftRepository.delete(shift);
                return true;
            } throw new UserMismatchException("User mismatch");
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }
}
