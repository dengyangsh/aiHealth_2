package com.aihealth.first.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CycleExerciseDomain {
    private Long id; // 运动记录ID
    private Long cycleId; // 健身周期ID
    private LocalDate exerciseDate; // 运动日期
    private LocalTime exerciseTime; // 运动时间
    private String exerciseContent; // 运动内容
    private Integer durationMinutes; // 运动时长（分钟）
    private Double caloriesBurned; // 消耗卡路里
    private String exerciseDescription; // 运动的介绍
    private LocalDateTime plannedExerciseTime; // 计划运动时间

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public LocalDate getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(LocalDate exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public LocalTime getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(LocalTime exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public String getExerciseContent() {
        return exerciseContent;
    }

    public void setExerciseContent(String exerciseContent) {
        this.exerciseContent = exerciseContent;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getExerciseDescription() {
        return exerciseDescription;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }

    public LocalDateTime getPlannedExerciseTime() {
        return plannedExerciseTime;
    }

    public void setPlannedExerciseTime(LocalDateTime plannedExerciseTime) {
        this.plannedExerciseTime = plannedExerciseTime;
    }
} 