package com.aihealth.first.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aihealth.first.entity.User;
import com.aihealth.first.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    
    /**
     * 绑定用户设备
     * @param userId 用户ID
     * @param scaleDeviceId 体重秤设备ID
     * @param speakerDeviceId 音箱设备ID
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}/bind-devices")
    public ResponseEntity<User> bindDevices(@PathVariable Long userId,
                                            @RequestParam String scaleDeviceId,
                                            @RequestParam String speakerDeviceId) {
        User updatedUser = userService.bindDevice(userId, scaleDeviceId, speakerDeviceId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 上传用户身体数据
     * @param userId 用户ID
     * @param height 身高(cm)
     * @param weight 体重(kg)
     * @param bodyFatRate 体脂率(%)
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}/upload-data")
    public ResponseEntity<User> uploadUserData(@PathVariable Long userId,
                                               @RequestParam Double height,
                                               @RequestParam Double weight,
                                               @RequestParam Double bodyFatRate) {
        User updatedUser = userService.uploadUserData(userId, height, weight, bodyFatRate);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新用户全部身体数据
     * @param userId 用户ID
     * @param age 年龄
     * @param height 身高(cm)
     * @param weight 体重(kg)
     * @param bodyFatRate 体脂率(%)
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}/update-data")
    public ResponseEntity<User> updateUserData(@PathVariable Long userId,
                                               @RequestParam Integer age,
                                               @RequestParam Double height,
                                               @RequestParam Double weight,
                                               @RequestParam Double bodyFatRate) {
        User updatedUser = userService.updateUserData(userId, age, height, weight, bodyFatRate);
        return ResponseEntity.ok(updatedUser);
    }
}