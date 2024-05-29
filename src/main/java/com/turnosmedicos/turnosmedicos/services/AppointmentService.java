package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.exceptions.UserMismatchException;
import com.turnosmedicos.turnosmedicos.models.Appointment;
import com.turnosmedicos.turnosmedicos.models.DayOfService;
import com.turnosmedicos.turnosmedicos.models.Doctor;
import com.turnosmedicos.turnosmedicos.models.User;
import com.turnosmedicos.turnosmedicos.repositories.AppointmentRepository;
import com.turnosmedicos.turnosmedicos.repositories.DayOfServiceRepository;
import com.turnosmedicos.turnosmedicos.repositories.DoctorRepository;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class AppointmentService {
    private final AuthService authService;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DayOfServiceRepository dayOfServiceRepository;

    @Autowired
    public AppointmentService(AuthService authService, AppointmentRepository appointmentRepository, UserRepository userRepository, DoctorRepository doctorRepository, DayOfServiceRepository dayOfServiceRepository) {
        this.authService = authService;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.dayOfServiceRepository = dayOfServiceRepository;
    }

    public List<Appointment> getAllAppointment(String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            return appointmentRepository.findByUserId(userId);
        } throw new UnauthorizedException("Unauthorized: Invalid token");
    }

    public String getDayAppointmentAvailable(String token, Long id) {
        if(authService.validationToken(token)) {

            for(Integer i=0; i<7 ; i++) {
                LocalDate dateNow = LocalDate.now().plusDays(i);
                String day = dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase();
                DayOfService dayOfService = dayOfServiceRepository.findByDoctorIdAndDay(id, day);
                if(dayOfService != null) {
                    return dayOfService.getDay();
                }
            }

        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    /*public List<Date> getAllAppointmentAvailable(String token, Date date) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));

        } throw new UnauthorizedException("Unauthorized: invalid token");
    }*/

    public Appointment addAppointment(String token, Appointment appointment) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new ResourceNotFoundException("The doctor is not found"));

            Appointment newAppointment = new Appointment();
            newAppointment.setUser(user);
            newAppointment.setDoctor(doctor);
            newAppointment.setDate(appointment.getDate());
            newAppointment.setSpeciality(appointment.getSpeciality());

            return appointmentRepository.save(newAppointment);
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public Boolean cancelAppointment(Long id, String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("The user with this id:" + id + "is not found"));
            Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("The shift with this id: " + id + "is not found"));;
            if(Objects.equals(user.getId(), appointment.getUser().getId())) {
                appointmentRepository.delete(appointment);
                return true;
            } throw new UserMismatchException("User mismatch");
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }
}
