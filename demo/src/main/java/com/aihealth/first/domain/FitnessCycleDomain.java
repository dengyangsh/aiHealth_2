package com.aihealth.first.domain;

import com.aihealth.first.entity.FitnessCycle;
import java.time.LocalDateTime;

public class FitnessCycleDomain {
    private Long id; // 健身周期ID
    private Long userId; // 用户ID
    private String cycleNumber; // 健身周期编号
    private FitnessCycle.CycleStatus status; // 健身周期状态
    private LocalDateTime startTime; // 健身周期开始时间
    private LocalDateTime endTime; // 健身周期结束时间
    private Integer durationDays; // 健身周期持续时间（天）
    private String goal; // 健身周期的目标
    private Double startWeight; // 健身周期开始体重
    private Double endWeight; // 健身周期结束体重

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(String cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public FitnessCycle.CycleStatus getStatus() {
        return status;
    }

    public void setStatus(FitnessCycle.CycleStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Double getStartWeight() {
        return startWeight;
    }

    public void setStartWeight(Double startWeight) {
        this.startWeight = startWeight;
    }

    public Double getEndWeight() {
        return endWeight;
    }

    public void setEndWeight(Double endWeight) {
        this.endWeight = endWeight;
    }
} 