package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cycle_diets")
public class CycleDiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 饮食记录ID

    @Column(name = "cycle_id", nullable = false)
    private Long cycleId; // 健身周期ID

    @Column(name = "food_date", nullable = false)
    private LocalDate foodDate; // 食物日期

    @Column(name = "meal_time", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealTime mealTime; // 用餐时间

    @Column(name = "food_quantity")
    private Double foodQuantity; // 食物量（克）

    @Column(name = "food_category", nullable = false)
    private String foodCategory; // 食物类别

    @Column(name = "calories")
    private Double calories; // 食物的卡路里

    @Column(name = "cooking_method")
    private String cookingMethod; // 食物的做法

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    @Column(name = "diet_content")
    private String dietContent; // 饮食内容

    @Column(name = "calories_intake")
    private Double caloriesIntake; // 摄入卡路里

    @Column(name = "diet_description")
    private String dietDescription; // 饮食描述

    public enum MealTime {
        BREAKFAST,           // 早餐
        MORNING_SNACK,      // 早上加餐
        LUNCH,              // 午餐
        AFTERNOON_SNACK,    // 下午加餐
        DINNER,             // 晚餐
        EVENING_SNACK       // 晚上加餐
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setDietContent(String dietContent) {
        this.dietContent = dietContent;
    }

    public void setCaloriesIntake(Double caloriesIntake) {
        this.caloriesIntake = caloriesIntake;
    }

    public void setDietDescription(String dietDescription) {
        this.dietDescription = dietDescription;
    }
} 