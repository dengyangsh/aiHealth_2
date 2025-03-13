package com.aihealth.first.domain;

import com.aihealth.first.entity.CycleFeedback;
import java.time.LocalDateTime;

public class CycleFeedbackDomain {
    private String id; // 反馈ID，UUID
    private String userId; // 用户ID
    private String cycleId; // 健身周期ID
    private LocalDateTime feedbackTime; // 反馈提交时间
    private CycleFeedback.FeedbackType feedbackType; // 反馈类型
    private CycleFeedback.FeedbackSource feedbackSource; // 反馈来源
    private Integer completionRate; // 运动完成度（百分比，0-100）
    private String dietDeviation; // 饮食偏差详情（JSON格式）
    private Integer bodyStatusScore; // 身体状态评分（1-5）
    private String subjectiveFeeling; // 主观感受描述
    private CycleFeedback.SatisfactionLevel satisfactionLevel; // 满意度评级
    private String attachedData; // 关联数据链接（JSON格式）
    private Boolean isProcessed; // 处理状态
    private String processedAction; // 系统/人工处理结果
    private Boolean emergencyFlag; // 紧急标记
    private String followUpNotes; // 后续跟进备注

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public LocalDateTime getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(LocalDateTime feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public CycleFeedback.FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(CycleFeedback.FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public CycleFeedback.FeedbackSource getFeedbackSource() {
        return feedbackSource;
    }

    public void setFeedbackSource(CycleFeedback.FeedbackSource feedbackSource) {
        this.feedbackSource = feedbackSource;
    }

    public Integer getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Integer completionRate) {
        this.completionRate = completionRate;
    }

    public String getDietDeviation() {
        return dietDeviation;
    }

    public void setDietDeviation(String dietDeviation) {
        this.dietDeviation = dietDeviation;
    }

    public Integer getBodyStatusScore() {
        return bodyStatusScore;
    }

    public void setBodyStatusScore(Integer bodyStatusScore) {
        this.bodyStatusScore = bodyStatusScore;
    }

    public String getSubjectiveFeeling() {
        return subjectiveFeeling;
    }

    public void setSubjectiveFeeling(String subjectiveFeeling) {
        this.subjectiveFeeling = subjectiveFeeling;
    }

    public CycleFeedback.SatisfactionLevel getSatisfactionLevel() {
        return satisfactionLevel;
    }

    public void setSatisfactionLevel(CycleFeedback.SatisfactionLevel satisfactionLevel) {
        this.satisfactionLevel = satisfactionLevel;
    }

    public String getAttachedData() {
        return attachedData;
    }

    public void setAttachedData(String attachedData) {
        this.attachedData = attachedData;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public String getProcessedAction() {
        return processedAction;
    }

    public void setProcessedAction(String processedAction) {
        this.processedAction = processedAction;
    }

    public Boolean getEmergencyFlag() {
        return emergencyFlag;
    }

    public void setEmergencyFlag(Boolean emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }

    public String getFollowUpNotes() {
        return followUpNotes;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }
} 