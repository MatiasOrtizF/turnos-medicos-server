package com.turnosmedicos.turnosmedicos.repositories;

import com.turnosmedicos.turnosmedicos.models.DayOfService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayOfServiceRepository extends JpaRepository<DayOfService, Long> {
    //List<DayOfService> findByDoctorId(Long userId);
    DayOfService findByDoctorIdAndDay(Long doctorId, String day);
}
