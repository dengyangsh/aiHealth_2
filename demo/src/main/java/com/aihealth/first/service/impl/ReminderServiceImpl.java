package com.aihealth.first.service.impl;

import com.aihealth.first.entity.CycleDiet;
import com.aihealth.first.entity.CycleExercise;
import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.repository.CycleDietRepository;
import com.aihealth.first.repository.CycleExerciseRepository;
import com.aihealth.first.repository.FitnessCycleRepository;
import com.aihealth.first.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private CycleDietRepository cycleDietRepository;

    @Autowired
    private CycleExerciseRepository cycleExerciseRepository;

    // 每天晚上8点提醒用户购买食物
    @Scheduled(cron = "0 0 20 * * ?")
    @Override
    public void remindToBuyFood() {
        List<FitnessCycle> activeCycles = fitnessCycleRepository.findByStatus(FitnessCycle.CycleStatus.ACTIVE);
        for (FitnessCycle cycle : activeCycles) {
            List<CycleDiet> diets = cycleDietRepository.findByCycleId(cycle.getId());
            if (!diets.isEmpty()) {
                for (CycleDiet diet : diets) {
                    // 这里添加提醒用户购买食物的逻辑
                    System.out.println("提醒用户购买食物: " + diet.getDietContent());
                }
            }
        }
    }

    // 每5分钟检查运动时间
    @Scheduled(fixedRate = 300000)
    @Override
    public void checkExerciseTime() {
        List<FitnessCycle> activeCycles = fitnessCycleRepository.findByStatus(FitnessCycle.CycleStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        for (FitnessCycle cycle : activeCycles) {
            List<CycleExercise> exercises = cycleExerciseRepository.findByCycleId(cycle.getId());
            if (!exercises.isEmpty()) {
                for (CycleExercise exercise : exercises) {
                    // 从 exerciseDate 和 exerciseTime 组合成 LocalDateTime
                    if (exercise.getExerciseDate() != null && exercise.getExerciseTime() != null) {
                        LocalDateTime plannedExerciseTime = LocalDateTime.of(
                                exercise.getExerciseDate(), 
                                exercise.getExerciseTime()
                        );
                        
                        if (now.isAfter(plannedExerciseTime.minusMinutes(5)) && now.isBefore(plannedExerciseTime.plusMinutes(5))) {
                            // 这里添加提醒用户运动的逻辑
                            System.out.println("提醒用户运动: " + exercise.getExerciseContent());
                        }
                    }
                }
            }
        }
    }
} 