package com.turnosmedicos.turnosmedicos.repositories;

import com.turnosmedicos.turnosmedicos.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserId(Long id);
}
