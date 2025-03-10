package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 用户ID

    @Column(nullable = false, unique = true)
    private String username; // 用户名

    @Column(nullable = false)
    private Integer age; // 用户年龄

    @Column(nullable = false)
    private Double height; // 身高，单位：厘米

    @Column(nullable = false)
    private Double weight; // 体重，单位：千克

    @Column(name = "body_fat_rate")
    private Double bodyFatRate; // 体脂率，百分比

    @Column(name = "scale_device_id")
    private String scaleDeviceId; // 电子秤设备ID

    @Column(name = "speaker_device_id")
    private String speakerDeviceId; // 智能音箱设备ID

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

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