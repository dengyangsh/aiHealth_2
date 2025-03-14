package com.aihealth.first.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class CycleExerciseDto {

    private Long id; // 主键

    private LocalDate exerciseDate; // 运动日期

    private LocalTime exerciseTime; // 运动时间

    private String exerciseContent; // 运动内容

    private Integer durationMinutes; // 运动时长（分钟）

    private Double caloriesBurned; // 消耗卡路里

    private String exerciseDescription; // 运动的介绍



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
} 