package com.aihealth.first.domain;

import com.aihealth.first.entity.CycleDiet;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CycleDietDomain {
    private Long id; // 饮食记录ID
    private Long cycleId; // 健身周期ID
    private LocalDate foodDate; // 食物日期
    private CycleDiet.MealTime mealTime; // 用餐时间
    private Double foodQuantity; // 食物量（克）
    private String foodCategory; // 食物类别
    private Double calories; // 食物的卡路里
    private String cookingMethod; // 食物的做法
    private String dietContent; // 饮食内容
    private Double caloriesIntake; // 摄入卡路里
    private String dietDescription; // 饮食描述

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

    public LocalDate getFoodDate() {
        return foodDate;
    }

    public void setFoodDate(LocalDate foodDate) {
        this.foodDate = foodDate;
    }

    public CycleDiet.MealTime getMealTime() {
        return mealTime;
    }

    public void setMealTime(CycleDiet.MealTime mealTime) {
        this.mealTime = mealTime;
    }

    public Double getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(Double foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getCookingMethod() {
        return cookingMethod;
    }

    public void setCookingMethod(String cookingMethod) {
        this.cookingMethod = cookingMethod;
    }

    public String getDietContent() {
        return dietContent;
    }

    public void setDietContent(String dietContent) {
        this.dietContent = dietContent;
    }

    public Double getCaloriesIntake() {
        return caloriesIntake;
    }

    public void setCaloriesIntake(Double caloriesIntake) {
        this.caloriesIntake = caloriesIntake;
    }

    public String getDietDescription() {
        return dietDescription;
    }

    public void setDietDescription(String dietDescription) {
        this.dietDescription = dietDescription;
    }
}