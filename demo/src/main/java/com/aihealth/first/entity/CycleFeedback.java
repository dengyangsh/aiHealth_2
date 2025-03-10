package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cycle_feedbacks")
public class CycleFeedback {
    @Id
    @Column(name = "feedback_id")
    private String id; // 反馈ID，UUID

    @Column(name = "user_id", nullable = false)
    private String userId; // 用户ID

    @Column(name = "cycle_id")
    private String cycleId; // 健身周期ID

    @Column(name = "feedback_time", nullable = false)
    private LocalDateTime feedbackTime; // 反馈提交时间

    @Column(name = "feedback_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType; // 反馈类型

    @Column(name = "feedback_source", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackSource feedbackSource; // 反馈来源

    @Column(name = "completion_rate")
    private Integer completionRate; // 运动完成度（百分比，0-100）

    @Column(name = "diet_deviation")
    @Lob
    private String dietDeviation; // 饮食偏差详情（JSON格式）

    @Column(name = "body_status_score")
    private Integer bodyStatusScore; // 身体状态评分（1-5）

    @Column(name = "subjective_feeling")
    @Lob
    private String subjectiveFeeling; // 主观感受描述

    @Column(name = "satisfaction_level")
    @Enumerated(EnumType.STRING)
    private SatisfactionLevel satisfactionLevel; // 满意度评级

    @Column(name = "attached_data")
    @Lob
    private String attachedData; // 关联数据链接（JSON格式）

    @Column(name = "is_processed")
    private Boolean isProcessed; // 处理状态

    @Column(name = "processed_action")
    @Lob
    private String processedAction; // 系统/人工处理结果

    @Column(name = "emergency_flag")
    private Boolean emergencyFlag; // 紧急标记

    @Column(name = "follow_up_notes")
    @Lob
    private String followUpNotes; // 后续跟进备注

    public enum FeedbackType {
        EXERCISE_PLAN,    // 运动计划
        DIET_PLAN,       // 饮食计划
        BODY_STATUS,     // 身体状态
        DEVICE_ISSUE,    // 设备问题
        OTHER           // 其他
    }

    public enum FeedbackSource {
        APP,             // APP
        SMART_SPEAKER,   // 智能音箱
        WEARABLE_DEVICE, // 可穿戴设备
        CUSTOMER_SERVICE // 人工客服
    }

    public enum SatisfactionLevel {
        VERY_DISSATISFIED, // 非常不满
        DISSATISFIED,      // 不满
        NEUTRAL,           // 一般
        SATISFIED,         // 满意
        VERY_SATISFIED     // 非常满意
    }

    @PrePersist
    protected void onCreate() {
        feedbackTime = LocalDateTime.now();
    }
} 