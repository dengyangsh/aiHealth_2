package com.aihealth.first.repository;

import com.aihealth.first.entity.CycleExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CycleExerciseRepository extends JpaRepository<CycleExercise, Long> {
    List<CycleExercise> findByCycleId(Long cycleId);
} 