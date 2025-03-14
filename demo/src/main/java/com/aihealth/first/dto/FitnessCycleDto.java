package com.aihealth.first.dto;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class FitnessCycleDto {

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 健身周期开始时间

    @Column(name = "end_time")
    private LocalDateTime endTime; // 健身周期结束时间

    @Column(name = "duration_days")
    private Integer durationDays; // 健身周期持续时间（天）

    private String goal; // 健身周期的目标

    @Column(name = "start_weight")
    private Double startWeight; // 健身周期开始体重

    public enum CycleStatus {
        CONFIRMING, // 确认中
        PLANNED,    // 计划中
        ACTIVE,     // 进行中
        COMPLETED,  // 已完成
        CANCELLED   // 已取消
    }
} 