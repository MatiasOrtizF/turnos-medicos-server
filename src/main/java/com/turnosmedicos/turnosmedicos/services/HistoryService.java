package com.turnosmedicos.turnosmedicos.services;

import com.turnosmedicos.turnosmedicos.exceptions.ResourceNotFoundException;
import com.turnosmedicos.turnosmedicos.exceptions.UnauthorizedException;
import com.turnosmedicos.turnosmedicos.models.Appointment;
import com.turnosmedicos.turnosmedicos.models.History;
import com.turnosmedicos.turnosmedicos.repositories.AppointmentRepository;
import com.turnosmedicos.turnosmedicos.repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final AuthService authService;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository, AuthService authService, AppointmentRepository appointmentRepository) {
        this.historyRepository = historyRepository;
        this.authService = authService;
        this.appointmentRepository = appointmentRepository;
    }

    public List<History> getHistory(String token) {
        if(authService.validationToken(token)) {
            Long userId = authService.getUserId(token);
            return historyRepository.findByUserId(userId);
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }

    public History addHistory(String token, Long id) {
        if(authService.validationToken(token)) {
            Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("The appointment with this id: " + id  +  "is not found"));

            History newHistory = new History();
            newHistory.setUser(appointment.getUser());
            newHistory.setDoctor(appointment.getDoctor());
            newHistory.setDay(appointment.getDay());
            newHistory.setHour(appointment.getHour());

            appointmentRepository.deleteById(id);

            return historyRepository.save(newHistory);
        } throw new UnauthorizedException("Unauthorized: invalid token");
    }
}
