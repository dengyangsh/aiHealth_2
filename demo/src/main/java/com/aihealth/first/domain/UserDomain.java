package com.aihealth.first.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

public class UserDomain {
    private Long id; // 用户ID
    
    @NotBlank(message = "用户名不能为空")
    private String username; // 用户名
    
    @NotBlank(message = "密码不能为空")
    private String password; // 密码
    
    @NotNull(message = "年龄不能为空")
    private Integer age; // 用户年龄
    @NotNull(message = "身高不能为空")
    private Double height; // 身高，单位：厘米
    
    private Double weight; // 体重，单位：千克
    
    private Double bodyFatRate; // 体脂率，百分比
    private String scaleDeviceId; // 电子秤设备ID
    private String speakerDeviceId; // 智能音箱设备ID
    private String question; // 用户提问
    private String modificationRequest; // 修改请求

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getBodyFatRate() {
        return bodyFatRate;
    }

    public void setBodyFatRate(Double bodyFatRate) {
        this.bodyFatRate = bodyFatRate;
    }

    public String getScaleDeviceId() {
        return scaleDeviceId;
    }

    public void setScaleDeviceId(String scaleDeviceId) {
        this.scaleDeviceId = scaleDeviceId;
    }

    public String getSpeakerDeviceId() {
        return speakerDeviceId;
    }

    public void setSpeakerDeviceId(String speakerDeviceId) {
        this.speakerDeviceId = speakerDeviceId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModificationRequest() {
        return modificationRequest;
    }

    public void setModificationRequest(String modificationRequest) {
        this.modificationRequest = modificationRequest;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 