package com.aihealth.first.repository;

import com.aihealth.first.entity.CycleDiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CycleDietRepository extends JpaRepository<CycleDiet, Long> {
    Optional<CycleDiet> findByCycleId(Long cycleId);
} 