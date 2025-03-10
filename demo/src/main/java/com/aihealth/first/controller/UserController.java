package com.aihealth.first.controller;

import com.aihealth.first.entity.User;
import com.aihealth.first.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建成功的用户信息
     */
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.save(user);
        return ResponseEntity.ok(createdUser);
    }

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

    /**
     * 通过音箱更新用户身体数据
     * 使用本地ollama deepSeek模型处理用户语音输入:
     * 1. 分析用户语音内容，判断是否需要更新数据
     * 2. 识别需要更新的具体数据项(身高/体重/体脂率)
     * 3. 仅在确认需要更新时才执行更新操作
     * 
     * @param userId 用户ID
     * @param speakerInput 用户语音输入
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}/update-from-speaker")
    public ResponseEntity<User> updateUserDataFromSpeaker(@PathVariable Long userId,
                                                          @RequestParam String speakerInput) {
        User updatedUser = userService.updateUserDataFromSpeaker(userId, speakerInput);
        return ResponseEntity.ok(updatedUser);
    }
}