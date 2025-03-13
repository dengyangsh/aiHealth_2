package com.aihealth.first.service;

import com.aihealth.first.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    List<User> findAll();
    
    Optional<User> findById(Long id);
    
    User save(User user);
    
    User login(String username, String password);
    
    void deleteById(Long id);
    
    User bindDevice(Long userId, String scaleDeviceId, String speakerDeviceId);
    
    User uploadUserData(Long userId, Double height, Double weight, Double bodyFatRate);
    
    User updateUserData(Long userId, Integer age, Double height, Double weight, Double bodyFatRate);
} 