package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fitness_cycles")
public class FitnessCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 健身周期ID

    @Column(name = "user_id", nullable = false)
    private Long userId; // 用户ID

    @Column(name = "cycle_number", nullable = false)
    private String cycleNumber; // 健身周期编号

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CycleStatus status; // 健身周期状态

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // 健身周期开始时间

    @Column(name = "end_time")
    private LocalDateTime endTime; // 健身周期结束时间

    @Column(name = "duration_days")
    private Integer durationDays; // 健身周期持续时间（天）

    private String goal; // 健身周期的目标

    @Column(name = "start_weight")
    private Double startWeight; // 健身周期开始体重

    @Column(name = "end_weight")
    private Double endWeight; // 健身周期结束体重

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    public enum CycleStatus {
        CONFIRMING, // 确认中
        PLANNED,    // 计划中
        ACTIVE,     // 进行中
        COMPLETED,  // 已完成
        CANCELLED   // 已取消
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
} 