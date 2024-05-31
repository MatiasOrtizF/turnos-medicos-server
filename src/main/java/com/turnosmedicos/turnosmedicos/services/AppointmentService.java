package com.turnosmedicos.turnosmedicos.services;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.exceptions.UserMismatchException;
import com.turnosmedicos.turnosmedicos.models.*;
import com.turnosmedicos.turnosmedicos.repositories.AppointmentRepository;
import com.turnosmedicos.turnosmedicos.repositories.DayOfServiceRepository;
import com.turnosmedicos.turnosmedicos.repositories.DoctorRepository;
import com.turnosmedicos.turnosmedicos.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

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


    public AppointmentResponse getAllAppointmentAvailable(String token, Long id) {
        if(authService.validationToken(token)) {
            List<LocalTime> listHour = new ArrayList<>();
            String dayAvailable = "";

            int i = 0;

            while(listHour.isEmpty()){
                LocalDate dateNow = LocalDate.now().plusDays(i);
                String day = dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase();

                List<DayOfService> dayOfServices = dayOfServiceRepository.findByDoctorId(id);

                if(dayOfServices != null) {
                    for(DayOfService dayOfService : dayOfServices) {
                        if(day.equals(dayOfService.getDay().toLowerCase())) {
                            dayAvailable = dateNow.toString();
                            List<Appointment> appointments = appointmentRepository.findByDay(dateNow);
                            LocalTime startTime = dayOfService.getStartTime();
                            LocalTime endTime = dayOfService.getEndTime();

                            long minDif = ChronoUnit.MINUTES.between(startTime, endTime);

                            for(int j = 0; j<=minDif; j+=15) {
                                listHour.add(startTime);
                                startTime = startTime.plusMinutes(15);
                            }

                            if (appointments != null) {
                                for (Appointment appointment : appointments) {
                                    for(int k = 0; k<listHour.size(); k++) {
                                        if(appointment.getHour().toString().equals(listHour.get(k).toString())) {
                                            listHour.remove(k);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                i++;
            }

            AppointmentResponse response = new AppointmentResponse();
            response.setDay(dayAvailable);
            response.setHour(listHour);

            return response; // return appointment available
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }


    public Appointment addAppointment(String token, Appointment appointment) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new ResourceNotFoundException("The doctor is not found"));

            Appointment newAppointment = new Appointment();
            newAppointment.setUser(user);
            newAppointment.setDoctor(doctor);
            newAppointment.setDay(appointment.getDay());
            newAppointment.setHour(appointment.getHour());
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
