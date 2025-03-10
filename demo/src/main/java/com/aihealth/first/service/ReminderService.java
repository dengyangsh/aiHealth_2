package com.aihealth.first.service;

import com.aihealth.first.entity.CycleDiet;
import com.aihealth.first.entity.CycleExercise;
import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.repository.CycleDietRepository;
import com.aihealth.first.repository.CycleExerciseRepository;
import com.aihealth.first.repository.FitnessCycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private CycleDietRepository cycleDietRepository;

    @Autowired
    private CycleExerciseRepository cycleExerciseRepository;

    // 每天晚上8点提醒用户购买食物
    @Scheduled(cron = "0 0 20 * * ?")
    public void remindToBuyFood() {
        List<FitnessCycle> activeCycles = fitnessCycleRepository.findByStatus(FitnessCycle.CycleStatus.ACTIVE);
        for (FitnessCycle cycle : activeCycles) {
            Optional<CycleDiet> diet = cycleDietRepository.findByCycleId(cycle.getId());
            diet.ifPresent(cycleDiet -> {
                // 这里添加提醒用户购买食物的逻辑
                System.out.println("提醒用户购买食物: " + cycleDiet.getDietContent());
            });
        }
    }

    // 每5分钟检查运动时间
    @Scheduled(fixedRate = 300000)
    public void checkExerciseTime() {
        List<FitnessCycle> activeCycles = fitnessCycleRepository.findByStatus(FitnessCycle.CycleStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        for (FitnessCycle cycle : activeCycles) {
            Optional<CycleExercise> exercise = cycleExerciseRepository.findByCycleId(cycle.getId());
            exercise.ifPresent(cycleExercise -> {
                // 假设有一个方法来获取计划的运动时间
                LocalDateTime plannedExerciseTime = cycleExercise.getPlannedExerciseTime();
                if (now.isAfter(plannedExerciseTime.minusMinutes(5)) && now.isBefore(plannedExerciseTime.plusMinutes(5))) {
                    // 这里添加提醒用户运动的逻辑
                    System.out.println("提醒用户运动: " + cycleExercise.getExerciseContent());
                }
            });
        }
    }
} 