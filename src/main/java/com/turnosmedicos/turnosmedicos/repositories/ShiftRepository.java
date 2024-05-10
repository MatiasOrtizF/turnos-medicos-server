package com.turnosmedicos.turnosmedicos.repositories;

import com.turnosmedicos.turnosmedicos.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
