package com.aihealth.first.controller;

import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fitness")
public class FitnessController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/create-cycle")
    public ResponseEntity<FitnessCycle> createFitnessCycle(@PathVariable Long userId) {
        FitnessCycle fitnessCycle = userService.createFitnessCycle(userId);
        return ResponseEntity.ok(fitnessCycle);
    }

    @PutMapping("/update-cycle/{cycleId}")
    public ResponseEntity<FitnessCycle> updateFitnessCycle(@PathVariable Long cycleId, @RequestBody FitnessCycle updatedCycle) {
        FitnessCycle fitnessCycle = userService.updateFitnessCycle(cycleId, updatedCycle);
        return ResponseEntity.ok(fitnessCycle);
    }
} 