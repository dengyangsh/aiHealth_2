package com.aihealth.first.service;

import com.aihealth.first.entity.CycleDiet;
import com.aihealth.first.entity.CycleExercise;
import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.repository.CycleDietRepository;
import com.aihealth.first.repository.CycleExerciseRepository;
import com.aihealth.first.repository.FitnessCycleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ModificationService {

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private CycleDietRepository cycleDietRepository;

    @Autowired
    private CycleExerciseRepository cycleExerciseRepository;

    public void processModificationResponse(Long userId, String response) throws Exception {
        // 解析返回的JSON字符串
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Object>> updates = mapper.readValue(response, new TypeReference<Map<String, Map<String, Object>>>() {});

        // 更新饮食计划
        if (updates.containsKey("diet")) {
            Map<String, Object> dietUpdates = updates.get("diet");
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId, FitnessCycle.CycleStatus.ACTIVE);
            if (activeCycle.isPresent()) {
                CycleDiet cycleDiet = cycleDietRepository.findByCycleId(activeCycle.get().getId())
                    .orElseThrow(() -> new RuntimeException("未找到饮食计划"));
                
                if (dietUpdates.containsKey("dietContent")) {
                    cycleDiet.setDietContent((String)dietUpdates.get("dietContent"));
                }
                if (dietUpdates.containsKey("caloriesIntake")) {
                    cycleDiet.setCaloriesIntake((Double)dietUpdates.get("caloriesIntake"));
                }
                if (dietUpdates.containsKey("dietDescription")) {
                    cycleDiet.setDietDescription((String)dietUpdates.get("dietDescription")); 
                }
                cycleDietRepository.save(cycleDiet);
            }
        }

        // 更新运动计划
        if (updates.containsKey("exercise")) {
            Map<String, Object> exerciseUpdates = updates.get("exercise");
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId, FitnessCycle.CycleStatus.ACTIVE);
            if (activeCycle.isPresent()) {
                CycleExercise cycleExercise = cycleExerciseRepository.findByCycleId(activeCycle.get().getId())
                    .orElseThrow(() -> new RuntimeException("未找到运动计划"));
                
                if (exerciseUpdates.containsKey("exerciseContent")) {
                    cycleExercise.setExerciseContent((String)exerciseUpdates.get("exerciseContent"));
                }
                if (exerciseUpdates.containsKey("durationMinutes")) {
                    cycleExercise.setDurationMinutes((Integer)exerciseUpdates.get("durationMinutes"));
                }
                if (exerciseUpdates.containsKey("caloriesBurned")) {
                    cycleExercise.setCaloriesBurned((Double)exerciseUpdates.get("caloriesBurned"));
                }
                if (exerciseUpdates.containsKey("exerciseDescription")) {
                    cycleExercise.setExerciseDescription((String)exerciseUpdates.get("exerciseDescription"));
                }
                cycleExerciseRepository.save(cycleExercise);
            }
        }

        // 更新健身周期
        if (updates.containsKey("fitness")) {
            Map<String, Object> fitnessUpdates = updates.get("fitness");
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId, FitnessCycle.CycleStatus.ACTIVE);
            if (activeCycle.isPresent()) {
                FitnessCycle cycle = activeCycle.get();
                
                if (fitnessUpdates.containsKey("goal")) {
                    cycle.setGoal((String)fitnessUpdates.get("goal"));
                }
                if (fitnessUpdates.containsKey("endWeight")) {
                    cycle.setEndWeight((Double)fitnessUpdates.get("endWeight"));
                }
                if (fitnessUpdates.containsKey("status")) {
                    cycle.setStatus(FitnessCycle.CycleStatus.valueOf((String)fitnessUpdates.get("status")));
                }
                fitnessCycleRepository.save(cycle);
            }
        }
    }
} 