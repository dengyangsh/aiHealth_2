package com.aihealth.first.dto;

import lombok.Data;

@Data
public class UserDto {

    private Integer age; // 用户年龄

    private Double height; // 身高，单位：厘米

    private Double weight; // 体重，单位：千克

    private Double bodyFatRate; // 体脂率，百分比

 
} 