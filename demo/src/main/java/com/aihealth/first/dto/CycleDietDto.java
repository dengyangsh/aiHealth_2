package com.aihealth.first.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CycleDietDto {

    private Long id; // 主键

    private LocalDate foodDate; // 食物日期

    private MealTime mealTime; // 用餐时间

    private Double foodQuantity; // 食物量（克）

    private String foodCategory; // 食物类别

    private Double calories; // 食物的卡路里

    private String cookingMethod; // 食物的做法

    private String dietContent; // 饮食内容

    private Double caloriesIntake; // 摄入卡路里

    private String dietDescription; // 饮食描述

    public enum MealTime {
        BREAKFAST,           // 早餐
        MORNING_SNACK,      // 早上加餐
        LUNCH,              // 午餐
        AFTERNOON_SNACK,    // 下午加餐
        DINNER,             // 晚餐
        EVENING_SNACK       // 晚上加餐
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