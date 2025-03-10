package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "cycle_exercises")
public class CycleExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 运动记录ID

    @Column(name = "cycle_id", nullable = false)
    private Long cycleId; // 健身周期ID

    @Column(name = "exercise_date", nullable = false)
    private LocalDate exerciseDate; // 运动日期

    @Column(name = "exercise_time", nullable = false)
    private LocalTime exerciseTime; // 运动时间

    @Column(name = "exercise_content", nullable = false)
    private String exerciseContent; // 运动内容

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // 运动时长（分钟）

    @Column(name = "calories_burned")
    private Double caloriesBurned; // 消耗卡路里

    @Column(name = "exercise_description")
    @Lob
    private String exerciseDescription; // 运动的介绍

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    @Column(name = "planned_exercise_time")
    private LocalDateTime plannedExerciseTime; // 计划运动时间

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 添加getter和setter方法
    public void setExerciseDate(LocalDate exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public void setExerciseTime(LocalTime exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public void setExerciseContent(String exerciseContent) {
        this.exerciseContent = exerciseContent;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }

    public LocalDateTime getPlannedExerciseTime() {
        // 假设有一个字段plannedExerciseTime
        return plannedExerciseTime;
    }
} 