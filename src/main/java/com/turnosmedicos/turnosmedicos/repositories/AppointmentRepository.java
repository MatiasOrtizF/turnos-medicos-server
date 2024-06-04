package com.turnosmedicos.turnosmedicos.repositories;

import com.turnosmedicos.turnosmedicos.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByDay(LocalDate day);
    Appointment findByDoctorIdAndHourAndDay(Long doctorId, LocalTime hour, LocalDate day);
}
