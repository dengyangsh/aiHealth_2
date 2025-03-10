package com.aihealth.first.repository;

import com.aihealth.first.entity.CycleExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CycleExerciseRepository extends JpaRepository<CycleExercise, Long> {
    Optional<CycleExercise> findByCycleId(Long cycleId);
} 