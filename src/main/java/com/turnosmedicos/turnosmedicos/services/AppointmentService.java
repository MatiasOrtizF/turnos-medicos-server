package com.turnosmedicos.turnosmedicos.services;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.turnosmedicos.turnosmedicos.exceptions.AppointmentAlreadyExistingException;
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


    public AppointmentResponse getAllAppointmentAvailable(String token, Long id, Integer dayNumber) {
        if(authService.validationToken(token)) {
            List<LocalTime> listHour = new ArrayList<>(); // inicializamos la lista de horas disponibles
            AppointmentResponse response = getNextDayAvailable(token, id, dayNumber);
            String dayAvailable = response.getDay();
            LocalDate dateNow = LocalDate.parse(dayAvailable);

            while(listHour.isEmpty()){ // mientras la lista sea vacia

                List<Appointment> appointments = appointmentRepository.findByDay(dateNow); // lista de turnos no disponibles del dia recorrido

                listHour = getNextDayAvailable(token, id, dayNumber).getHour();

                if (appointments != null) { // si la lista es nula significa que estan todos los turnos libres.
                    for (Appointment appointment : appointments) { // recorremos la lista de turnos
                        for(int k = 0; k<listHour.size(); k++) { // recorremos la lista de horas (cada 15 minutos, es lo que dura cada consulta)
                            if(appointment.getHour().toString().equals(listHour.get(k).toString())) { // si el turno ya ocupa es igual a una hora recorrida esa hora se quita de la lista.
                                listHour.remove(k); // removemos la hora de la lista.
                            }
                        }
                    }
                }
            }

            response.setHour(listHour);

            return response; // return appointment available
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public AppointmentResponse getNextDayAvailable(String token, Long id, Integer dayNumber) {
        if (authService.validationToken(token)) {
            List<LocalTime> listHour = new ArrayList<>(); // inicializamos la lista de horas disponibles
            dayNumber++;
            int i = dayNumber; // la i representa el dia.
            int cantAppointmentPerDay = 0;

            while(i>=0){ // mientras la lista sea vacia
                LocalDate dateNow = LocalDate.now().plusDays(i); //el dia recorrido
                String day = dateNow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(); //dia recorrido formateado

                List<DayOfService> dayOfServices = dayOfServiceRepository.findByDoctorId(id); // lista de dias que atiende el doctor

                if(dayOfServices != null) { // la lista de dias no es nulo
                    for(DayOfService dayOfService : dayOfServices) {  // recorremos los dias que atiende el doctor
                        if(day.equals(dayOfService.getDay().toLowerCase())) { // si el dia recorrido coincide al dia que atiende el doctor
                            String dayAvailable = dateNow.toString(); //dayAvailable va a ser igual a dia recorrido
                            List<Appointment> appointments = appointmentRepository.findByDay(dateNow); // lista de turnos no disponibles del dia recorrido
                            LocalTime startTime = dayOfService.getStartTime(); // hora de entrada del doctor
                            LocalTime endTime = dayOfService.getEndTime(); // hora de salida del doctor

                            long minDif = ChronoUnit.MINUTES.between(startTime, endTime); // minutos total que trabaja el doctor (para poder saber hasta que punto recorrer)


                            for(int j = 0; j<=minDif; j+=15) { // recorremos las horas que el doctor atiende (cada 15 minutos, es lo que dura cada consulta)
                                cantAppointmentPerDay++;
                                listHour.add(startTime); // agregamos la hora al al array
                                startTime = startTime.plusMinutes(15); // aumenta la hora para recorrer el ciclo for
                            }

                            if(appointments.isEmpty() || appointments.size() < cantAppointmentPerDay) { // si esto es true significa que estan todos los turnos libres.
                                AppointmentResponse response = new AppointmentResponse();
                                response.setDayNumber(i);
                                response.setDay(dayAvailable);
                                response.setHour(listHour);

                                return response;
                            }
                        }
                    }
                }

                i++; // aumentamos i para que ir al dia siguiente.
            }
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public String getPreviousDayAvailable(String token, Long id) {
        if (authService.validationToken(token)) {
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

            return dayAvailable; // return day available
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }


    public Appointment addAppointment(String token, Appointment appointment) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user is not found"));
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new ResourceNotFoundException("The doctor is not found"));

            Appointment appointmentExisting = appointmentRepository.findByDoctorIdAndHourAndDay(appointment.getDoctor().getId(), appointment.getHour(), appointment.getDay());
            if(appointmentExisting == null) {
                Appointment newAppointment = new Appointment();

                newAppointment.setUser(user);
                newAppointment.setDoctor(doctor);
                newAppointment.setDay(appointment.getDay());
                newAppointment.setHour(appointment.getHour());

                return appointmentRepository.save(newAppointment);
            } throw new AppointmentAlreadyExistingException("The appointment already existing");
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public Map<String, Boolean> cancelAppointment(Long id, String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("The user with this id:" + id + "is not found"));
            Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("The appointment with this id: " + id + "is not found"));;

            if(Objects.equals(user.getId(), appointment.getUser().getId())) {
                appointmentRepository.delete(appointment);

                Map<String, Boolean> response = new HashMap<>();
                response.put("deleted", Boolean.TRUE);
                return response;
            } throw new UserMismatchException("User mismatch");
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }
}
