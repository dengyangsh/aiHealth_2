package com.aihealth.first.service;

/**
 * 提醒服务接口
 */
public interface ReminderService {

    /**
     * 提醒用户购买食物
     */
    void remindToBuyFood();
    
    /**
     * 检查运动时间并提醒用户
     */
    void checkExerciseTime();
}