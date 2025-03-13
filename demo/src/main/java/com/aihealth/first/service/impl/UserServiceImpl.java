package com.aihealth.first.service.impl;

import com.aihealth.first.entity.User;
import com.aihealth.first.repository.UserRepository;
import com.aihealth.first.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     * @throws RuntimeException 如果用户不存在或密码错误
     */
    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                throw new RuntimeException("密码错误");
            }
        }
        throw new RuntimeException("用户不存在");
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User bindDevice(Long userId, String scaleDeviceId, String speakerDeviceId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setScaleDeviceId(scaleDeviceId);
            user.setSpeakerDeviceId(speakerDeviceId);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User uploadUserData(Long userId, Double height, Double weight, Double bodyFatRate) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setHeight(height);
            user.setWeight(weight);
            user.setBodyFatRate(bodyFatRate);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User updateUserData(Long userId, Integer age, Double height, Double weight, Double bodyFatRate) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAge(age);
            user.setHeight(height);
            user.setWeight(weight);
            user.setBodyFatRate(bodyFatRate);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
}