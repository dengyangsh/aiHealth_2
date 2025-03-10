package com.aihealth.first.repository;

import com.aihealth.first.entity.FitnessCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FitnessCycleRepository extends JpaRepository<FitnessCycle, Long> {
    Optional<FitnessCycle> findByUserIdAndStatus(Long userId, FitnessCycle.CycleStatus status);
    List<FitnessCycle> findByStatus(FitnessCycle.CycleStatus status);
} 