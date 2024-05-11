package com.turnosmedicos.turnosmedicos.repositories;

import com.turnosmedicos.turnosmedicos.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByUserId(Long userId);
}
